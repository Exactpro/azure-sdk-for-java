package com.microsoft.windowsazure.services.servicebus;

import com.microsoft.windowsazure.core.pipeline.jersey.JerseyFilterableService;
import com.microsoft.windowsazure.exception.ServiceException;
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
import com.sun.jersey.api.client.ClientResponse;

import rx.Observable;

public interface ServiceBusContractAsync extends
JerseyFilterableService<ServiceBusContract> {

/**
* Sends a queue message.
* 
* @param queuePath
*            A <code>String</code> object that represents the name of the
*            queue to which the message will be sent.
* @param message
*            A <code>Message</code> object that represents the message to
*            send.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> sendQueueMessage(String queuePath, BrokeredMessage message);

/**
* Receives a queue message.
* 
* @param queuePath
*            A <code>String</code> object that represents the name of the
*            queue from which to receive the message.
* @return A <code>Observable&lt;ReceiveQueueMessageResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ReceiveQueueMessageResult> receiveQueueMessage(String queuePath);

/**
* Receives a queue message using the specified receive message options.
* 
* @param queuePath
*            A <code>String</code> object that represents the name of the
*            queue from which to receive the message.
* @param options
*            A <code>ReceiveMessageOptions</code> object that represents
*            the receive message options.
* @return A <code>Observable&lt;ReceiveQueueMessageResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ReceiveQueueMessageResult> receiveQueueMessage(String queuePath,
    ReceiveMessageOptions options);

/**
* Sends a topic message.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic to which the message will be sent.
* @param message
*            A <code>Message</code> object that represents the message to
*            send.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> sendTopicMessage(String topicPath, BrokeredMessage message);

/**
* Receives a subscription message.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic to receive.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription from the message will be received.
* @return A <code>Observable&lt;ReceiveSubscriptionMessageResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ReceiveSubscriptionMessageResult> receiveSubscriptionMessage(
    String topicPath, String subscriptionName);

/**
* Receives a subscription message using the specified receive message
* options.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic to receive.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription from the message will be received.
* @param options
*            A <code>ReceiveMessageOptions</code> object that represents
*            the receive message options.
* @return A <code>Observable&lt;ReceiveSubscriptionMessageResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ReceiveSubscriptionMessageResult> receiveSubscriptionMessage(
    String topicPath, String subscriptionName,
    ReceiveMessageOptions options);

/**
* Unlocks a message.
* 
* @param message
*            A <code>Message</code> object that represents the message to
*            unlock.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> unlockMessage(BrokeredMessage message);

/**
* Sends a message.
* 
* @param path
*            A <code>String</code> object that represents the path to which
*            the message will be sent. This may be the value of a queuePath
*            or a topicPath.
* @param message
*            A <code>Message</code> object that represents the message to
*            send.
* 
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> sendMessage(String path, BrokeredMessage message);

/**
* Receives a message.
* 
* @param path
*            A <code>String</code> object that represents the path from
*            which a message will be received. This may either be the value
*            of queuePath or a combination of the topicPath +
*            "/subscriptions/" + subscriptionName.
* @return A <code>Observable&lt;ReceiveMessageResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ReceiveMessageResult> receiveMessage(String path);

/**
* Receives a message using the specified receive message options.
* 
* @param path
*            A <code>String</code> object that represents the path from
*            which a message will be received. This may either be the value
*            of queuePath or a combination of the topicPath +
*            "/subscriptions/" + subscriptionName.
* @param options
*            A <code>ReceiveMessageOptions</code> object that represents
*            the receive message options.
* @return A <code>Observable&lt;ReceiveMessageResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ReceiveMessageResult> receiveMessage(String path,
    ReceiveMessageOptions options);

/**
* Deletes a message.
* 
* @param message
*            A <code>Message</code> object that represents the message to
*            delete.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> deleteMessage(BrokeredMessage message);

/**
* Creates a queue.
* 
* @param queueInfo
*            A <code>QueueInfo</code> object that represents the queue to
*            create.
* @return A <code>Observable&lt;CreateQueueResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<CreateQueueResult> createQueue(QueueInfo queueInfo);

/**
* Deletes a queue.
* 
* @param queuePath
*            A <code>String</code> object that represents the name of the
*            queue to delete.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> deleteQueue(String queuePath);

/**
* Retrieves a queue.
* 
* @param queuePath
*            A <code>String</code> object that represents the name of the
*            queue to retrieve.
* @return A <code>Observable&lt;GetQueueResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<GetQueueResult> getQueue(String queuePath);

/**
* Returns a list of queues.
* 
* @return A <code>Observable&lt;ListQueuesResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListQueuesResult> listQueues();

/**
* Returns a list of queues.
* 
* @param options
*            A <code>ListQueueOptions</code> object that represents the
*            options to list the queue.
* @return A <code>Observable&lt;ListQueuesResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListQueuesResult> listQueues(ListQueuesOptions options);

/**
* Updates the information of a queue.
* 
* @param queueInfo
*            The information of a queue to be updated.
* 
* @return A <code>Observable&lt;QueueInfo&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<QueueInfo> updateQueue(QueueInfo queueInfo);

/**
* Creates a topic.
* 
* @param topic
*            A <code>Topic</code> object that represents the topic to
*            create.
* @return A <code>Observable&lt;CreateTopicResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<CreateTopicResult> createTopic(TopicInfo topic);

/**
* Deletes a topic.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            queue to delete.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> deleteTopic(String topicPath);

/**
* Retrieves a topic.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic to retrieve.
* @return A <code>Observable&lt;GetTopicResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<GetTopicResult> getTopic(String topicPath);

/**
* Returns a list of topics.
* 
* @return A <code>Observable&lt;ListTopicsResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListTopicsResult> listTopics();

/**
* Returns a list of topics.
* 
* @param options
*            A <code>ListTopicsOptions</code> object that represents the
*            options to list the topic.
* @return A <code>Observable&lt;ListTopicsResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListTopicsResult> listTopics(ListTopicsOptions options);

/**
* Updates a topic.
* 
* @param topicInfo
*            A <code>TopicInfo</code> object that represents the topic to
*            be updated.
* 
* @return A <code>Observable&lt;TopicInfo&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<TopicInfo> updateTopic(TopicInfo topicInfo);

/**
* Creates a subscription.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscription
*            A <code>Subscription</code> object that represents the
*            subscription to create.
* @return A <code>Observable&lt;CreateSubscriptionResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<CreateSubscriptionResult> createSubscription(String topicPath,
    SubscriptionInfo subscription);

/**
* Deletes a subscription.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription to delete.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> deleteSubscription(String topicPath, String subscriptionName);

/**
* Retrieves a subscription.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription to retrieve.
* @return A <code>Observable&lt;GetSubscriptionResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<GetSubscriptionResult> getSubscription(String topicPath,
    String subscriptionName);

/**
* Returns a list of subscriptions.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscriptions to retrieve.
* @return A <code>Observable&lt;ListSubscriptionsResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListSubscriptionsResult> listSubscriptions(String topicPath);

/**
* Returns a list of subscriptions.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscriptions to retrieve.
* 
* @param options
*            A <code>ListSubscriptionsOptions</code> object that represents
*            the options to list subscriptions.
* 
* @return A <code>Observable&lt;ListSubscriptionsResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListSubscriptionsResult> listSubscriptions(String topicPath,
    ListSubscriptionsOptions options);

/**
* Updates a subscription.
* 
* @param topicName
*            A <code>String</code> option which represents the name of the
*            topic.
* @param subscriptionInfo
*            A <code>SubscriptionInfo</code> option which represents the
*            information of the subscription.
* @return A <code>Observable&lt;SubscriptionInfo&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<SubscriptionInfo> updateSubscription(String topicName,
    SubscriptionInfo subscriptionInfo);

/**
* Creates a rule.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription for which the rule will be created.
* @param rule
*            A <code>Rule</code> object that represents the rule to create.
* @return A <code>Observable&lt;CreateRuleResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<CreateRuleResult> createRule(String topicPath, String subscriptionName,
    RuleInfo rule);

/**
* Deletes a rule.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription for which the rule will be deleted.
* @param ruleName
*            A <code>String</code> object that represents the name of the
*            rule to delete.
* @return A <code>Observable&lt;Void&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<Void> deleteRule(String topicPath, String subscriptionName, String ruleName);

/**
* Retrieves a rule.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription for which the rule will be retrieved.
* @param ruleName
*            A <code>String</code> object that represents the name of the
*            rule to retrieve.
* @return A <code>Observable&lt;GetRuleResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<GetRuleResult> getRule(String topicPath, String subscriptionName,
    String ruleName);

/**
* Returns a list of rules.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription whose rules are being retrieved.
* @return A <code>Observable&lt;ListRulesResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListRulesResult> listRules(String topicPath, String subscriptionName);

/**
* Returns a list of rules.
* 
* @param topicPath
*            A <code>String</code> object that represents the name of the
*            topic for the subscription.
* @param subscriptionName
*            A <code>String</code> object that represents the name of the
*            subscription whose rules are being retrieved.
* @param options
*            A <code>ListRulesOptions</code> object that represents the
*            options to retrieve rules.
* @return A <code>Observable&lt;ListRulesResult&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ListRulesResult> listRules(String topicPath, String subscriptionName,
    ListRulesOptions options);

/**
* Renew queue lock.
* 
* @param queueName
*            A <code>String</code> object that represents the name of the
*            queue.
* @param messageId
*            A <code>String</code> object that represents the ID of the
*            message.
* @param lockToken
*            A <code>String</code> object that represents the token of the
*            lock.
* @return A <code>Observable&lt;ClientResponse&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ClientResponse> renewQueueLock(String queueName, String messageId, String lockToken)
    throws ServiceException;

/**
* Renew subscription lock.
* 
* @param topicName
*            A <code>String</code> object that represents the name of the
*            topic.
* @param queueName
*            A <code>String</code> object that represents the name of the
*            queue.
* @param messageId
*            A <code>String</code> object that represents the ID of the
*            message.
* @param lockToken
*            A <code>String</code> object that represents the token of the
*            lock.
* @return A <code>Observable&lt;ClientResponse&gt;</code> object that represents
*         the result.
*         If a service exception is encountered the returned Observable invokes onError passing ServiceException into it.
*/
Observable<ClientResponse> renewSubscriptionLock(String topicName, String subscriptionName,
    String messageId, String lockToken) throws ServiceException;
}
