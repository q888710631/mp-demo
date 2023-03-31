package com.honyee.app.config.nacos;

import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.listener.Subscriber;

/**
 * 实例变动事件
 */
public class NacosInstancesChangeNotifier extends Subscriber<InstancesChangeEvent> {
    @Override
    public void onEvent(InstancesChangeEvent event) {
        // do something
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
}
