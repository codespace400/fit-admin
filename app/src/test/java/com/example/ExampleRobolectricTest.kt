package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.entity.MemberEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FitAdmin", appName)
  }

  @Test
  fun `member expiry logic check`() {
    val now = 1000000L
    val activeMember = MemberEntity(
      name = "John Doe",
      phone = "1234567890",
      email = "john@example.com",
      planId = 1,
      planName = "Pro Monthly",
      startDate = now - 50000L,
      expiryDate = now + 50000L
    )
    assertFalse(activeMember.isExpired(now))

    val expiredMember = MemberEntity(
      name = "Jane Doe",
      phone = "1234567891",
      email = "jane@example.com",
      planId = 1,
      planName = "Pro Monthly",
      startDate = now - 100000L,
      expiryDate = now - 10000L
    )
    assertTrue(expiredMember.isExpired(now))
  }
}
