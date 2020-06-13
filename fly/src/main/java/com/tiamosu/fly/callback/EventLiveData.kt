package com.tiamosu.fly.callback

/**
 * 描述：用于 callback 的情况，配合 MutableLiveData & SharedViewModel 的使用，
 * 发送完成后会把发送的对象自动销毁，防止内存增加
 *
 * @author tiamosu
 * @date 2020/6/13.
 */
class EventLiveData<T> : EventBaseLiveData<T> {

    /**
     * Creates a MutableLiveData initialized with the given `value`.
     *
     * @param value initial value
     */
    constructor(value: Event<T>) : super(value)

    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    constructor() : super()

    fun postValue(value: T) {
        super.postEvent(Event(value))
    }

    var value: T?
        get() = super.getValue()?.getContent()
        set(value) = super.setEvent(Event(value))
}