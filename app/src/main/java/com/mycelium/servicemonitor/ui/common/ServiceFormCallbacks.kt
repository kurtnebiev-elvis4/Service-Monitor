package com.mycelium.servicemonitor.ui.common

/**
 * Interface for all callbacks needed by the ServiceForm component.
 * This organizes multiple callbacks into a single place for cleaner code.
 */
interface ServiceFormCallbacks {
    // Text input callbacks
    fun onNameChange(value: String)
    fun onUrlChange(value: String)
    fun onIntervalChange(value: Int)
    fun onMethodChange(value: String)
    fun onBodyChange(value: String)
    fun onSha1CertificateChange(value: String)
    fun onResponsePatternChange(value: String)
    fun onUseRegexPatternChange(value: Boolean)
    fun onGroupNameChange(value: String)
    
    // Action callbacks
    fun onSave()
    fun onCancel()
    fun onCreateNewGroup()
    fun onAddHeader()
    fun onRemoveHeader(header: Pair<String, String>)
}

/**
 * Implementation helper class that makes it easier to create
 * callback implementations with default behavior.
 */
abstract class ServiceFormCallbacksImpl : ServiceFormCallbacks {
    override fun onNameChange(value: String) {}
    override fun onUrlChange(value: String) {}
    override fun onIntervalChange(value: Int) {}
    override fun onMethodChange(value: String) {}
    override fun onBodyChange(value: String) {}
    override fun onSha1CertificateChange(value: String) {}
    override fun onResponsePatternChange(value: String) {}
    override fun onUseRegexPatternChange(value: Boolean) {}
    override fun onGroupNameChange(value: String) {}
    override fun onSave() {}
    override fun onCancel() {}
    override fun onCreateNewGroup() {}
    override fun onAddHeader() {}
    override fun onRemoveHeader(header: Pair<String, String>) {}
}