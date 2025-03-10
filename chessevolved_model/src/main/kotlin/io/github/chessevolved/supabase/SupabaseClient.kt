package io.github.chessevolved.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

object SupabaseClient {
    private val supabase : SupabaseClient = createSupabaseClient(
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

    public fun getSupabaseClient(): SupabaseClient {
        return supabase
    }
}
