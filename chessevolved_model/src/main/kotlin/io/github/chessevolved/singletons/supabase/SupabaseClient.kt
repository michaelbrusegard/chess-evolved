package io.github.chessevolved.singletons.supabase

import io.github.cdimascio.dotenv.dotenv
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

object SupabaseClient {
    private val dotenv = dotenv()
    private val supabase: SupabaseClient =
        createSupabaseClient(
            supabaseUrl = dotenv["SUPABASE_URL"] ?: "no_url_found",
            // This key is an admin (service role) key.
            supabaseKey = dotenv["SUPABASE_ADMIN_KEY"] ?: "no_key_found",
        ) {
            install(Postgrest)
            install(Realtime) {
                reconnectDelay = 5.seconds // Default: 7 seconds
            }
            defaultLogLevel = LogLevel.INFO
            defaultSerializer =
                KotlinXSerializer(
                    Json {
                        // Custom serializer config
                    },
                )
        }

    fun getSupabaseClient(): SupabaseClient = supabase
}
