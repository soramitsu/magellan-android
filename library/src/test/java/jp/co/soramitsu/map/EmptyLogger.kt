package jp.co.soramitsu.map

class EmptyLogger : Logger {
    override fun log(tag: String, message: String) {}
    override fun log(tag: String, throwable: Throwable) {}
}