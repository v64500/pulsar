package ai.platon.pulsar.common.browser

import java.util.*

/**
 * The supported browser types
 */
enum class BrowserType {
    NATIVE, CHROME, MOCK_CHROME, PLAYWRIGHT_CHROME;

    override fun toString(): String {
        return name.lowercase(Locale.getDefault())
    }

    companion object {
        /**
         * Create a browser type from a string
         *
         * @param s the name of the browser type.
         * @return the BrowserType.
         */
        @JvmStatic
        fun fromString(name: String?): BrowserType {
            return if (name == null || name.isEmpty()) {
                CHROME
            } else try {
                valueOf(name.uppercase(Locale.getDefault()))
            } catch (e: Throwable) {
                CHROME
            }
        }
    }
}
