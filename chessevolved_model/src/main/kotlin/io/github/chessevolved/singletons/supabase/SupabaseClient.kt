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

internal object SupabaseClient {
    private val dotenv =
        dotenv {
            directory = "./"
            filename = "env"
            ignoreIfMissing = true
        }

    private val supabase: SupabaseClient =
        createSupabaseClient(
            supabaseUrl = "https://tsmubattgglbqaarktnw.supabase.co" ?: "no_url_found",
            supabaseKey =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRzbXViYXR0Z2dsYnFhYXJrdG53Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDIwNTE5MzksImV4cCI6MjA1NzYyNzkzOX0.O7DqYVCmuvHa3iOvXl53SOaNu-N6Rfb3E3J1Cg5K4Dw"
                    ?: "no_pass_found",
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
