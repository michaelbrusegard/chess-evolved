package io.github.chessevolved.supabase

import io.github.chessevolved.supabase.SupabaseClient.getSupabaseClient
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime

internal object SupabaseChannelSubscriber {
    /**
     * Supabase client used to query supabase.
     */
    private val supabase = getSupabaseClient()

    /**
     * Channels subscribed to.
     */
    private val channels : HashMap<String, RealtimeChannel> = HashMap<String, RealtimeChannel>()

    /**
     * Method subscribes to a specific channel.
     * @param channelName of channel to subscribe to
     * @return the RealtimeChannel subscribed to
     */
    private fun subscribeToChannel(channelName : String) : RealtimeChannel {
        if (!channels.containsKey(channelName)) {
            val channel = supabase.channel(channelName) {
                //optional config
            }
            return channel
        }
        throw Error("Unexpected behaviour when trying to subscribe to channel. Channel already exists.")
    }

    /**
     * Function to retrieve an already subscribed-to channel.
     * @param channelName of channel to return
     * @return RealtimeChannel of the subscribed to channel
     */
    private fun getChannel(channelName : String) : RealtimeChannel? {
        return channels[channelName]
    }

    /**
     * Method to either retrieve existing channel-subscription, or create a new subscription.
     * @param channelName of channel to retrieve/subscribe to
     * @return RealtimeChannel of the channel in question
     */
    fun getOrSubscribeToChannel(channelName : String) : RealtimeChannel {
        val channel = getChannel(channelName)
        if (channel != null) {
            return channel
        }

        return subscribeToChannel(channelName)
    }

    /**
     * Function for unsubscribing from channels.
     * @param channelName of channel to unsubscribe from
     */
    suspend fun unsubscribeFromChannel(channelName : String) {
        val channel = channels[channelName] ?: return

        // TODO: Error handling for when removing a channel fails.
        supabase.realtime.removeChannel(channel)
    }
}
