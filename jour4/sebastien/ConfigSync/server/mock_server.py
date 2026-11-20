"""Faux serveur REST pour tester ConfigSync sans le vrai serveur.

Lancer : python3 server/mock_server.py   (Python 3, rien à installer)
Depuis l'émulateur, ce serveur répond sur http://10.0.2.2:8080
Il vérifie la clé publique et la signature HMAC, exactement comme le ferait le vrai serveur.
"""
import hashlib
import hmac
import json
import time
from http.server import BaseHTTPRequestHandler, HTTPServer

# clé publique -> clé privée (le serveur connaît les deux)
KEYS = {"pub_demo_123": "priv_demo_456"}

PAIRS = {
    "nom_site": "Atelier Nord",
    "reseau": {"serveur": "10.0.0.12", "port": 502, "timeout_ms": 3000},
    "capteurs": {"temperature_max": 45, "intervalle_s": 30},
}


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/pairs":
            return self.reply(404, {"error": "chemin inconnu"})
        public_key = self.headers.get("X-Api-Key")
        timestamp = self.headers.get("X-Timestamp")
        signature = self.headers.get("X-Signature")
        private_key = KEYS.get(public_key)
        if not private_key or not timestamp or not signature:
            return self.reply(401, {"error": "clé publique inconnue ou en-têtes manquants"})
        if abs(time.time() - int(timestamp)) > 300:
            return self.reply(401, {"error": "horodatage expiré (horloge du téléphone ?)"})
        payload = f"{timestamp}\nGET\n/pairs".encode()
        expected = hmac.new(private_key.encode(), payload, hashlib.sha256).hexdigest()
        if not hmac.compare_digest(expected, signature):
            return self.reply(401, {"error": "signature invalide"})
        self.reply(200, PAIRS)

    def reply(self, code, body):
        data = json.dumps(body, ensure_ascii=False).encode()
        self.send_response(code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)


if __name__ == "__main__":
    print("Faux serveur sur http://localhost:8080/pairs (émulateur : http://10.0.2.2:8080)")
    HTTPServer(("0.0.0.0", 8080), Handler).serve_forever()
