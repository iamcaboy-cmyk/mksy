package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.SecurityUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Jan Sahayata", appName)
  }

  @Test
  fun `verify statutory fee requirement`() {
    val statutoryFee = 280.0
    assertEquals(280.0, statutoryFee, 0.001)
  }

  @Test
  fun `verify masked aadhaar display`() {
    val masked = SecurityUtil.formatMaskedAadhaar("4819")
    assertEquals("XXXX-XXXX-4819", masked)
  }
}
