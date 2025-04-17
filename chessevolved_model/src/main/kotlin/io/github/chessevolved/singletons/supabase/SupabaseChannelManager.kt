package io.github.chessevolved.singletons.supabase

import io.github.chessevolved.singletons.supabase.SupabaseClient.getSupabaseClient
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime

internal object SupabaseChannelManager {
    /**
     * Supabase client used to query supabase.
     */
    private val supabase = getSupabaseClient()

    /**
     * Channels subscribed to.
     */
    private val channels: HashMap<String, RealtimeChannel> = HashMap<String, RealtimeChannel>()

    /**
     * Method subscribes to a specific channel.
     * @param channelName of channel to subscribe to
     * @return the RealtimeChannel subscribed to
     * @throws Error if the channel already exists
     */
    private fun createChannel(channelName: String): RealtimeChannel {
        if (!channels.containsKey(channelName)) {
            val channel =
                supabase.channel(channelName) {
                    // optional config
                }
            channels[channelName] = channel
            return channel
        }
        throw Error("Unexpected behaviour when trying to subscribe to channel. Channel already exists.")
    }

    /**
     * Function to retrieve an already subscribed-to channel.
     * @param channelName of channel to return
     * @return RealtimeChannel of the subscribed to channel or null
     */
    fun getChannel(channelName: String): RealtimeChannel? = channels[channelName]

    /**
     * Check if a channel-name is already subscribed to.
     */
    fun channelExists(channelName: String): Boolean = channels.containsKey(channelName)

    /**
     * Method to either retrieve existing channel-subscription, or create a new subscription.
     * @param channelName of channel to retrieve/subscribe to
     * @return RealtimeChannel of the channel in question
     */
    fun getOrCreateChannel(channelName: String): RealtimeChannel {
        val channel = getChannel(channelName)
        if (channel != null) {
            return channel
        }
        return createChannel(channelName)
    }

    /**
     * Function for unsubscribing from channels.
     * @param channelName of channel to unsubscribe from
     * @throws Exception if trying to unsubscribe from nonexistent channel.
     */
    suspend fun unsubscribeFromChannel(channelName: String) {
        val channel = channels[channelName] ?: throw Exception("Can't unsubscribe from non-existent channel.")
        // TODO: Error handling for when removing a channel fails.
        supabase.realtime.removeChannel(channel)
        channels.remove(channelName)
    }
}
