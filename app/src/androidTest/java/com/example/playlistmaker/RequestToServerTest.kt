package com.example.playlistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.network.ITunesApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class RequestToServerTest {
    @MockK
    lateinit var networkClient: RetrofitNetworkClient
    @MockK
    lateinit var itunesApi: ITunesApi
    @MockK
    lateinit var context: Context
    @MockK
    lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        networkClient = RetrofitNetworkClient(itunesApi, context)
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun successful() = runBlocking {
        val request = TracksSearchRequest("test")
        val mockResponse = TracksSearchResponse("song", "test", emptyList()).apply { resultCode = 200 }

        val mockNetworkCapabilities = mockk<NetworkCapabilities>()

        every { connectivityManager.getNetworkCapabilities(any()) } returns mockNetworkCapabilities
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true

        coEvery { itunesApi.searchTrack("test") } returns mockResponse

        val response = networkClient.doRequest(request)

        assertEquals(200, response.resultCode)
        coVerify { itunesApi.searchTrack("test") }
    }

    @Test
    fun fails() = runBlocking {
        val request = TracksSearchRequest("test")

        val mockNetworkCapabilities = mockk<NetworkCapabilities>()

        every { connectivityManager.getNetworkCapabilities(any()) } returns mockNetworkCapabilities
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true

        coEvery { itunesApi.searchTrack("test") } throws Exception("Server error")

        val response = networkClient.doRequest(request)

        assertEquals(500, response.resultCode)
        coVerify { itunesApi.searchTrack("test") }
    }

    @Test
    fun noNetworkConnectivity() = runBlocking {
        val request = TracksSearchRequest("test")

        every { connectivityManager.getNetworkCapabilities(any()) } returns null

        val response = networkClient.doRequest(request)

        assertEquals(-1, response.resultCode)
        coVerify(exactly = 0) { itunesApi.searchTrack(any()) }
    }
}