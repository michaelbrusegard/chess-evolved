package io.github.chessevolved.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

object SupabaseClient {
    init {
        createSupabaseClient(
            supabaseUrl = "https://tsmubattgglbqaarktnw.supabase.co",

            // This key is an admin (service role) key.
            supabaseKey = ""
        ) {
            install(Postgrest)
            install(Realtime) {
                reconnectDelay = 5.seconds // Default: 7 seconds
            }
            defaultLogLevel = LogLevel.INFO
            defaultSerializer = KotlinXSerializer(Json {
                //Custom serializer config
            })
        }
        println("Supabase Client Initialized")
    }
}
