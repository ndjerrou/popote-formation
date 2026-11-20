package fr.formation.configsync.data

import org.junit.Assert.assertEquals
import org.junit.Test

class IniWriterTest {
    @Test
    fun `ecrit les sections et les paires`() {
        val ini = IniWriter.write(mapOf("reseau" to mapOf("port" to "502", "timeout" to "3000")), comment = "test")
        assertEquals("; test\n[reseau]\nport=502\ntimeout=3000\n\n", ini)
    }

    @Test
    fun `la signature est stable et en hexadecimal`() {
        val signature = RequestSigner.sign("priv_demo_456", 1_700_000_000, "GET", "/pairs")
        assertEquals(64, signature.length)                                     // SHA-256 = 32 octets = 64 caractères hexa
        assertEquals(signature, RequestSigner.sign("priv_demo_456", 1_700_000_000, "GET", "/pairs"))
    }
}
