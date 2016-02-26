/**
 * Copyright Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.windowsazure.services.servicebus.implementation;

import javax.inject.Inject;

import com.microsoft.windowsazure.core.UserAgentFilter;
import com.microsoft.windowsazure.core.pipeline.filter.ServiceRequestFilter;
import com.microsoft.windowsazure.core.pipeline.filter.ServiceResponseFilter;
import com.microsoft.windowsazure.core.pipeline.jersey.ServiceFilter;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContractAsync;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.CreateQueueResult;
import com.microsoft.windowsazure.services.servicebus.models.CreateRuleResult;
import com.microsoft.windowsazure.services.servicebus.models.CreateSubscriptionResult;
import com.microsoft.windowsazure.services.servicebus.models.CreateTopicResult;
import com.microsoft.windowsazure.services.servicebus.models.GetQueueResult;
import com.microsoft.windowsazure.services.servicebus.models.GetRuleResult;
import com.microsoft.windowsazure.services.servicebus.models.GetSubscriptionResult;
import com.microsoft.windowsazure.services.servicebus.models.GetTopicResult;
import com.microsoft.windowsazure.services.servicebus.models.ListQueuesOptions;
import com.microsoft.windowsazure.services.servicebus.models.ListQueuesResult;
import com.microsoft.windowsazure.services.servicebus.models.ListRulesOptions;
import com.microsoft.windowsazure.services.servicebus.models.ListRulesResult;
import com.microsoft.windowsazure.services.servicebus.models.ListSubscriptionsOptions;
import com.microsoft.windowsazure.services.servicebus.models.ListSubscriptionsResult;
import com.microsoft.windowsazure.services.servicebus.models.ListTopicsOptions;
import com.microsoft.windowsazure.services.servicebus.models.ListTopicsResult;
import com.microsoft.windowsazure.services.servicebus.models.QueueInfo;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.RuleInfo;
import com.microsoft.windowsazure.services.servicebus.models.SubscriptionInfo;
import com.microsoft.windowsazure.services.servicebus.models.TopicInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.ClientFilter;

import rx.Observable;

public class ServiceBusRestProxy implements ServiceBusContract {

    private ServiceBusRestProxyAsync asyncProxy;

    @Inject
    public ServiceBusRestProxy(Client channel, WrapFilter authFilter, SasFilter sasAuthFilter,
            UserAgentFilter userAgentFilter, ServiceBusConnectionSettings connectionSettings,
            BrokerPropertiesMapper mapper) {

        asyncProxy = new ServiceBusRestProxyAsync(channel, authFilter, sasAuthFilter, userAgentFilter,
                connectionSettings, mapper);
    }

    public ServiceBusRestProxy(Client channel, ClientFilter[] filters, String uri, BrokerPropertiesMapper mapper) {

        asyncProxy = new ServiceBusRestProxyAsync(channel, filters, uri, mapper);
    }

    ServiceBusRestProxy(ServiceBusRestProxyAsync async) {
        this.asyncProxy = async;
    }

    @Override
    public ServiceBusContractAsync async() {
        return asyncProxy;
    }

    @Override
    public ServiceBusContract withFilter(ServiceFilter filter) {
        return this.async().withFilter(filter);
    }

    @Override
    public ServiceBusContract withRequestFilterFirst(ServiceRequestFilter serviceRequestFilter) {
        return this.async().withRequestFilterFirst(serviceRequestFilter);
    }

    @Override
    public ServiceBusContract withRequestFilterLast(ServiceRequestFilter serviceRequestFilter) {
        return this.async().withRequestFilterLast(serviceRequestFilter);
    }

    @Override
    public ServiceBusContract withResponseFilterFirst(ServiceResponseFilter serviceResponseFilter) {
        return this.async().withResponseFilterFirst(serviceResponseFilter);
    }

    @Override
    public ServiceBusContract withResponseFilterLast(ServiceResponseFilter serviceResponseFilter) {
        return this.async().withResponseFilterLast(serviceResponseFilter);
    }

    public Client getChannel() {
        return this.asyncProxy.getChannel();
    }

    public void setChannel(Client channel) {
        this.asyncProxy.setChannel(channel);
    }

    @Override
    public void sendMessage(String path, BrokeredMessage message) throws ServiceException {
        resolve(this.async().sendMessage(path, message));
    }

    @Override
    public void sendQueueMessage(String path, BrokeredMessage message) throws ServiceException {
        sendMessage(path, message);
    }

    @Override
    public ReceiveQueueMessageResult receiveQueueMessage(String queueName) throws ServiceException {
        return receiveQueueMessage(queueName, ReceiveMessageOptions.DEFAULT);
    }

    @Override
    public ReceiveQueueMessageResult receiveQueueMessage(String queuePath, ReceiveMessageOptions options)
            throws ServiceException {
        return resolve(this.async().receiveQueueMessage(queuePath, options));
    }

    @Override
    public ReceiveMessageResult receiveMessage(String path) throws ServiceException {
        return receiveMessage(path, ReceiveMessageOptions.DEFAULT);
    }

    @Override
    public ReceiveMessageResult receiveMessage(String path, ReceiveMessageOptions options) throws ServiceException {
        return resolve(this.async().receiveMessage(path, options));
    }

    @Override
    public void sendTopicMessage(String topicName, BrokeredMessage message) throws ServiceException {
        sendMessage(topicName, message);
    }

    @Override
    public ReceiveSubscriptionMessageResult receiveSubscriptionMessage(String topicName, String subscriptionName)
            throws ServiceException {
        return receiveSubscriptionMessage(topicName, subscriptionName, ReceiveMessageOptions.DEFAULT);
    }

    @Override
    public ReceiveSubscriptionMessageResult receiveSubscriptionMessage(String topicName, String subscriptionName,
            ReceiveMessageOptions options) throws ServiceException {
        return resolve(this.async().receiveSubscriptionMessage(topicName, subscriptionName, options));
    }

    @Override
    public void unlockMessage(BrokeredMessage message) throws ServiceException {
        getChannel().resource(message.getLockLocation()).put("");
    }

    @Override
    public void deleteMessage(BrokeredMessage message) throws ServiceException {
        getChannel().resource(message.getLockLocation()).delete();
    }

    @Override
    public CreateQueueResult createQueue(QueueInfo queueInfo) throws ServiceException {
        return resolve(this.async().createQueue(queueInfo));
    }

    @Override
    public void deleteQueue(String queuePath) throws ServiceException {
        resolve(this.async().deleteQueue(queuePath));
    }

    @Override
    public GetQueueResult getQueue(String queuePath) throws ServiceException {
        return resolve(this.async().getQueue(queuePath));
    }

    @Override
    public ListQueuesResult listQueues(ListQueuesOptions options) throws ServiceException {
        return resolve(this.async().listQueues(options));
    }

    @Override
    public QueueInfo updateQueue(QueueInfo queueInfo) throws ServiceException {
        return resolve(this.async().updateQueue(queueInfo));
    }

    @Override
    public CreateTopicResult createTopic(TopicInfo entry) throws ServiceException {
        return resolve(this.async().createTopic(entry));
    }

    @Override
    public void deleteTopic(String topicPath) throws ServiceException {
        resolve(this.async().deleteTopic(topicPath));
    }

    @Override
    public GetTopicResult getTopic(String topicPath) throws ServiceException {
        return resolve(this.async().getTopic(topicPath));
    }

    @Override
    public ListTopicsResult listTopics(ListTopicsOptions options) throws ServiceException {
        return resolve(this.async().listTopics(options));
    }

    @Override
    public TopicInfo updateTopic(TopicInfo topicInfo) throws ServiceException {
        return resolve(this.async().updateTopic(topicInfo));
    }

    @Override
    public CreateSubscriptionResult createSubscription(String topicPath, SubscriptionInfo subscriptionInfo) {
        return resolve(this.async().createSubscription(topicPath, subscriptionInfo));
    }

    @Override
    public void deleteSubscription(String topicPath, String subscriptionName) {
        resolve(this.async().deleteSubscription(topicPath, subscriptionName));
    }

    @Override
    public GetSubscriptionResult getSubscription(String topicPath, String subscriptionName) {
        return resolve(this.async().getSubscription(topicPath, subscriptionName));
    }

    @Override
    public ListSubscriptionsResult listSubscriptions(String topicPath, ListSubscriptionsOptions options) {
        return resolve(this.async().listSubscriptions(topicPath, options));
    }

    @Override
    public SubscriptionInfo updateSubscription(String topicName, SubscriptionInfo subscriptionInfo)
            throws ServiceException {
        return resolve(this.async().updateSubscription(topicName, subscriptionInfo));
    }

    @Override
    public CreateRuleResult createRule(String topicPath, String subscriptionName, RuleInfo rule) {
        return resolve(this.async().createRule(topicPath, subscriptionName, rule));
    }

    @Override
    public void deleteRule(String topicPath, String subscriptionName, String ruleName) {
        resolve(this.async().deleteRule(topicPath, subscriptionName, ruleName));
    }

    @Override
    public GetRuleResult getRule(String topicPath, String subscriptionName, String ruleName) {
        return resolve(this.async().getRule(topicPath, subscriptionName, ruleName));
    }

    @Override
    public ListRulesResult listRules(String topicPath, String subscriptionName, ListRulesOptions options) {
        return resolve(this.async().listRules(topicPath, subscriptionName, options));
    }

    @Override
    public ListQueuesResult listQueues() throws ServiceException {
        return listQueues(ListQueuesOptions.DEFAULT);
    }

    @Override
    public ListTopicsResult listTopics() throws ServiceException {
        return listTopics(ListTopicsOptions.DEFAULT);
    }

    @Override
    public ListSubscriptionsResult listSubscriptions(String topicName) throws ServiceException {
        return listSubscriptions(topicName, ListSubscriptionsOptions.DEFAULT);
    }

    @Override
    public ListRulesResult listRules(String topicName, String subscriptionName) throws ServiceException {
        return listRules(topicName, subscriptionName, ListRulesOptions.DEFAULT);
    }

    @Override
    public void renewQueueLock(String queueName, String messageId, String lockToken) throws ServiceException {
        resolve(this.async().renewQueueLock(queueName, messageId, lockToken));
    }

    @Override
    public void renewSubscriptionLock(String topicName, String subscriptionName, String messageId, String lockToken)
            throws ServiceException {
        resolve(this.async().renewSubscriptionLock(topicName, subscriptionName, messageId, lockToken));
    }
    
    private <T> T resolve(Observable<T> o) {
        return o.toBlocking().single();
    }

}
