package com.fadixd.xdvpn.vpn

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject

object VmessConfig {

    private const val NETMOD_ADDRESS = "drugshortage.jp"
    private const val NETMOD_PORT = 443

    private const val NETMOD_ENCRYPTION = "auto"
    private const val NETMOD_ALTER_ID = 0

    private const val NETMOD_TRANSPORT = "grpc"
    private const val NETMOD_MODE = "gun"
    private const val NETMOD_SERVICE = "grpc"

    private const val NETMOD_TLS = "tls"
    private const val NETMOD_SNI = "sgray.jagoanip.my.id"

    fun build(vmessUrl: String): Result {

        require(vmessUrl.startsWith("vmess://")) {
            "INVALID_VMESS"
        }

        val encoded =
            vmessUrl.removePrefix("vmess://").trim()

        val json =
            JSONObject(decode(encoded))

        val uuid =
            json.optString("id").trim()

        require(uuid.isNotBlank()) {
            "VMESS_UUID_MISSING"
        }

        /*
         * NetMod 4.2.0:
         *
         * Protocol       = vmess
         * Hostname       = drugshortage.jp
         * Port           = 443
         * EncryptMethod   = auto
         * AlterID         = 0
         * TransferProtocol= grpc
         * FakeType        = gun
         * Path            = grpc
         * TLSType         = tls
         * SNI             = sgray.jagoanip.my.id
         * FingerPrint     = none
         * Alpn            = none
         *
         * gun = normal gRPC mode.
         * In Xray this is multiMode=false.
         */

        val grpcSettings =
            JSONObject()
                .put("serviceName", NETMOD_SERVICE)
                .put("multiMode", false)

        val tlsSettings =
            JSONObject()
                .put("serverName", NETMOD_SNI)

        /*
         * ALPN = none:
         * Do NOT add an "alpn" field.
         *
         * FingerPrint = none:
         * Do NOT add a fingerprint field.
         */

        val streamSettings =
            JSONObject()
                .put("network", NETMOD_TRANSPORT)
                .put("security", NETMOD_TLS)
                .put("grpcSettings", grpcSettings)
                .put("tlsSettings", tlsSettings)

        val user =
            JSONObject()
                .put("id", uuid)
                .put("alterId", NETMOD_ALTER_ID)
                .put("security", NETMOD_ENCRYPTION)
                .put("level", 0)

        val server =
            JSONObject()
                .put("address", NETMOD_ADDRESS)
                .put("port", NETMOD_PORT)
                .put(
                    "users",
                    JSONArray().put(user)
                )

        val outbound =
            JSONObject()
                .put("tag", "vmess-out")
                .put("protocol", "vmess")
                .put(
                    "settings",
                    JSONObject()
                        .put(
                            "vnext",
                            JSONArray().put(server)
                        )
                )
                .put("streamSettings", streamSettings)

        /*
         * Xray exposes a local SOCKS5 listener.
         * Hev reads Android TUN and sends traffic here.
         */

        val socksInbound =
            JSONObject()
                .put("tag", "local-socks")
                .put("listen", "127.0.0.1")
                .put("port", 10808)
                .put("protocol", "socks")
                .put(
                    "settings",
                    JSONObject()
                        .put("auth", "noauth")
                        .put("udp", true)
                )

        val config =
            JSONObject()
                .put(
                    "inbounds",
                    JSONArray().put(socksInbound)
                )
                .put(
                    "outbounds",
                    JSONArray().put(outbound)
                )

        return Result(
            config = config.toString(),
            serverAddress = NETMOD_ADDRESS
        )
    }

    private fun decode(value: String): String {

        val clean =
            value
                .replace("\n", "")
                .replace("\r", "")
                .trim()

        return try {

            String(
                Base64.decode(
                    clean,
                    Base64.DEFAULT
                ),
                Charsets.UTF_8
            )

        } catch (_: Exception) {

            String(
                Base64.decode(
                    clean,
                    Base64.URL_SAFE or
                        Base64.NO_WRAP or
                        Base64.NO_PADDING
                ),
                Charsets.UTF_8
            )
        }
    }

    data class Result(
        val config: String,
        val serverAddress: String
    )
}
