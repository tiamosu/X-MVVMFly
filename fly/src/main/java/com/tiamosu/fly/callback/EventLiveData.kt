package com.tiamosu.fly.callback

/**
 * 描述：为了在 "重回二级页面" 的场景下，解决 "数据倒灌" 的问题。
 *
 * 1.一条消息能被多个观察者消费
 * 2.延迟期结束，不再能够收到旧消息的推送
 * 3.并且旧消息在延迟期结束时能从内存中释放，避免内存溢出等问题
 *
 * @author tiamosu
 * @date 2020/7/10.
 */
class EventLiveData<T> : ProtectedEventLiveData<T>() {

    public override fun setValue(value: T?) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }

    class Builder<T> {

        /**
         * 消息的生存时长
         */
        private var eventSurvivalTime = 1000

        /**
         * 是否允许传入 null value
         */
        private var allowNullValue = false

        /**
         * 是否允许自动清理，默认 true
         */
        private var allowToClear = true

        fun setEventSurvivalTime(eventSurvivalTime: Int): Builder<T> {
            this.eventSurvivalTime = eventSurvivalTime
            return this
        }

        fun setAllowNullValue(allowNullValue: Boolean): Builder<T> {
            this.allowNullValue = allowNullValue
            return this
        }

        fun setAllowToClear(isAllowToClear: Boolean): Builder<T> {
            this.allowToClear = isAllowToClear
            return this
        }

        fun create(): EventLiveData<T> {
            return EventLiveData<T>().apply {
                delayToClearEvent = eventSurvivalTime
                isAllowNullValue = allowNullValue
                isAllowToClear = allowToClear
            }
        }
    }
}