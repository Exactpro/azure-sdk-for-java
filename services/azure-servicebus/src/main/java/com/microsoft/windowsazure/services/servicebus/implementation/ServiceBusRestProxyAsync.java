package com.microsoft.windowsazure.services.servicebus.implementation;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import com.microsoft.windowsazure.core.UserAgentFilter;
import com.microsoft.windowsazure.core.pipeline.PipelineHelpers;
import com.microsoft.windowsazure.core.pipeline.filter.ServiceRequestFilter;
import com.microsoft.windowsazure.core.pipeline.filter.ServiceResponseFilter;
import com.microsoft.windowsazure.core.pipeline.jersey.ClientFilterAdapter;
import com.microsoft.windowsazure.core.pipeline.jersey.ClientFilterRequestAdapter;
import com.microsoft.windowsazure.core.pipeline.jersey.ClientFilterResponseAdapter;
import com.microsoft.windowsazure.core.pipeline.jersey.ServiceFilter;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContractAsync;
import com.microsoft.windowsazure.services.servicebus.models.AbstractListOptions;
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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.AsyncWebResource.Builder;
import com.sun.jersey.api.client.filter.ClientFilter;

import rx.Observable;
import rx.functions.Func1;

public class ServiceBusRestProxyAsync implements ServiceBusContractAsync {

    private Client channel;
    private final String uri;
    private final BrokerPropertiesMapper mapper;
    private final CustomPropertiesMapper customPropertiesMapper;

    private ClientFilter[] filters;

    @Inject
    public ServiceBusRestProxyAsync(Client channel, WrapFilter authFilter, SasFilter sasAuthFilter,
            UserAgentFilter userAgentFilter, ServiceBusConnectionSettings connectionSettings,
            BrokerPropertiesMapper mapper) {

        this.channel = channel;
        this.filters = new ClientFilter[0];
        this.uri = connectionSettings.getUri();
        this.mapper = mapper;
        this.customPropertiesMapper = new CustomPropertiesMapper();
        if (connectionSettings.isSasAuthentication()) {
            channel.addFilter(sasAuthFilter);
        } else {
            channel.addFilter(authFilter);
        }
        channel.addFilter(new ClientFilterRequestAdapter(userAgentFilter));
    }

    public ServiceBusRestProxyAsync(Client channel, ClientFilter[] filters, String uri, BrokerPropertiesMapper mapper) {
        this.channel = channel;
        this.filters = filters;
        this.uri = uri;
        this.mapper = mapper;
        this.customPropertiesMapper = new CustomPropertiesMapper();
    }

    @Override
    public ServiceBusContract withFilter(ServiceFilter filter) {
        ClientFilter[] newFilters = Arrays.copyOf(filters, filters.length + 1);
        newFilters[filters.length] = new ClientFilterAdapter(filter);
        return new ServiceBusRestProxy(new ServiceBusRestProxyAsync(channel, newFilters, uri, mapper));
    }

    @Override
    public ServiceBusContract withRequestFilterFirst(ServiceRequestFilter serviceRequestFilter) {
        ClientFilter[] currentFilters = filters;
        ClientFilter[] newFilters = new ClientFilter[currentFilters.length + 1];
        System.arraycopy(currentFilters, 0, newFilters, 1, currentFilters.length);
        newFilters[0] = new ClientFilterRequestAdapter(serviceRequestFilter);
        return new ServiceBusRestProxy(new ServiceBusRestProxyAsync(channel, newFilters, uri, mapper));
    }

    @Override
    public ServiceBusContract withRequestFilterLast(ServiceRequestFilter serviceRequestFilter) {
        ClientFilter[] currentFilters = filters;
        ClientFilter[] newFilters = Arrays.copyOf(currentFilters, currentFilters.length + 1);
        newFilters[currentFilters.length] = new ClientFilterRequestAdapter(serviceRequestFilter);
        return new ServiceBusRestProxy(new ServiceBusRestProxyAsync(channel, newFilters, uri, mapper));
    }

    @Override
    public ServiceBusContract withResponseFilterFirst(ServiceResponseFilter serviceResponseFilter) {
        ClientFilter[] currentFilters = filters;
        ClientFilter[] newFilters = new ClientFilter[currentFilters.length + 1];
        System.arraycopy(currentFilters, 0, newFilters, 1, currentFilters.length);
        newFilters[0] = new ClientFilterResponseAdapter(serviceResponseFilter);
        return new ServiceBusRestProxy(new ServiceBusRestProxyAsync(channel, newFilters, uri, mapper));
    }

    @Override
    public ServiceBusContract withResponseFilterLast(ServiceResponseFilter serviceResponseFilter) {
        ClientFilter[] currentFilters = filters;
        ClientFilter[] newFilters = Arrays.copyOf(currentFilters, currentFilters.length + 1);
        newFilters[currentFilters.length] = new ClientFilterResponseAdapter(serviceResponseFilter);
        return new ServiceBusRestProxy(new ServiceBusRestProxyAsync(channel, newFilters, uri, mapper));
    }

    public Client getChannel() {
        return channel;
    }

    public void setChannel(Client channel) {
        this.channel = channel;
    }

    private AsyncWebResource getResource() {
        AsyncWebResource resource = getChannel().asyncResource(uri).queryParam("api-version", "2013-07");
        for (ClientFilter filter : filters) {
            resource.addFilter(filter);
        }
        return resource;
    }

    @Override
    public Observable<Void> sendMessage(String path, BrokeredMessage message) {
        Builder request = getResource().path(path).path("messages").getRequestBuilder();

        if (message.getContentType() != null) {
            request = request.type(message.getContentType());
        }

        if (message.getBrokerProperties() != null) {
            request = request.header("BrokerProperties", mapper.toString(message.getBrokerProperties()));
        }

        for (java.util.Map.Entry<String, Object> entry : message.getProperties().entrySet()) {
            request.header(entry.getKey(), customPropertiesMapper.toString(entry.getValue()));
        }

        Observable<Void> ret = Observable.<Void>from(request.post(Void.class, message.getBody()));

        return ret;
    }

    @Override
    public Observable<Void> sendQueueMessage(String path, BrokeredMessage message) {
        return sendMessage(path, message);
    }

    @Override
    public Observable<ReceiveQueueMessageResult> receiveQueueMessage(String queueName) {
        return receiveQueueMessage(queueName, ReceiveMessageOptions.DEFAULT);
    }

    @Override
    public Observable<ReceiveQueueMessageResult> receiveQueueMessage(String queuePath, ReceiveMessageOptions options) {

        AsyncWebResource resource = getResource().path(queuePath).path("messages").path("head");

        Observable<BrokeredMessage> message = receiveMessage(options, resource);
        return message.map(new Func1<BrokeredMessage, ReceiveQueueMessageResult>() {
            @Override
            public ReceiveQueueMessageResult call(BrokeredMessage msg) {
                return new ReceiveQueueMessageResult(msg);
            }
        });
    }

    @Override
    public Observable<ReceiveMessageResult> receiveMessage(String path) {
        return receiveMessage(path, ReceiveMessageOptions.DEFAULT);
    }

    @Override
    public Observable<ReceiveMessageResult> receiveMessage(String path, ReceiveMessageOptions options) {
        AsyncWebResource resource = getResource().path(path).path("messages").path("head");

        Observable<BrokeredMessage> message = receiveMessage(options, resource);
        return message.map(new Func1<BrokeredMessage, ReceiveMessageResult>() {
            @Override
            public ReceiveMessageResult call(BrokeredMessage msg) {
                return new ReceiveMessageResult(msg);
            }
        });
    }

    private Observable<BrokeredMessage> receiveMessage(ReceiveMessageOptions options, AsyncWebResource resource) {
        if (options.getTimeout() != null) {
            resource = resource.queryParam("timeout", Integer.toString(options.getTimeout()));
        }

        Future<ClientResponse> clientResult;
        if (options.isReceiveAndDelete()) {
            clientResult = resource.delete(ClientResponse.class);
        } else if (options.isPeekLock()) {
            clientResult = resource.post(ClientResponse.class, "");
        } else {
            throw new RuntimeException("Unknown ReceiveMode");
        }

        Observable<BrokeredMessage> observableResult = Observable.from(clientResult)
                .map(new Func1<ClientResponse, BrokeredMessage>() {
                    @Override
                    public BrokeredMessage call(ClientResponse clientResult) {
                        if (clientResult.getStatus() == 204) {
                            return null;
                        }

                        BrokerProperties brokerProperties;
                        if (clientResult.getHeaders().containsKey("BrokerProperties")) {
                            brokerProperties = mapper
                                    .fromString(clientResult.getHeaders().getFirst("BrokerProperties"));
                        } else {
                            brokerProperties = new BrokerProperties();
                        }

                        String location = clientResult.getHeaders().getFirst("Location");
                        if (location != null) {
                            brokerProperties.setLockLocation(location);
                        }

                        BrokeredMessage message = new BrokeredMessage(brokerProperties);

                        MediaType contentType = clientResult.getType();
                        if (contentType != null) {
                            message.setContentType(contentType.toString());
                        }

                        Date date = clientResult.getResponseDate();
                        if (date != null) {
                            message.setDate(date);
                        }

                        InputStream body = clientResult.getEntityInputStream();
                        if (body != null) {
                            message.setBody(body);
                        }

                        for (String key : clientResult.getHeaders().keySet()) {
                            Object value = clientResult.getHeaders().getFirst(key);
                            try {
                                value = customPropertiesMapper.fromString(value.toString());
                                message.setProperty(key, value);
                            } catch (ParseException e) {
                                // log.warn("Unable to parse custom header", e);
                            } catch (NumberFormatException e) {
                                // log.warn("Unable to parse custom header", e);
                            }
                        }

                        return message;
                    }
                });

        return observableResult;
    }

    @Override
    public Observable<Void> sendTopicMessage(String topicName, BrokeredMessage message) {
        return sendMessage(topicName, message);
    }

    @Override
    public Observable<ReceiveSubscriptionMessageResult> receiveSubscriptionMessage(String topicName,
            String subscriptionName) {
        return receiveSubscriptionMessage(topicName, subscriptionName, ReceiveMessageOptions.DEFAULT);
    }

    @Override
    public Observable<ReceiveSubscriptionMessageResult> receiveSubscriptionMessage(String topicName,
            String subscriptionName, ReceiveMessageOptions options) {
        AsyncWebResource resource = getResource().path(topicName).path("subscriptions").path(subscriptionName)
                .path("messages").path("head");

        Observable<BrokeredMessage> message = receiveMessage(options, resource);
        return message.map(new Func1<BrokeredMessage, ReceiveSubscriptionMessageResult>() {
            @Override
            public ReceiveSubscriptionMessageResult call(BrokeredMessage msg) {
                return new ReceiveSubscriptionMessageResult(msg);
            }
        });

    }

    @Override
    public Observable<Void> unlockMessage(BrokeredMessage message) {
        return Observable.<Void>from(getChannel().asyncResource(message.getLockLocation()).put(Void.class, ""));
    }

    @Override
    public Observable<Void> deleteMessage(BrokeredMessage message) {
        return Observable.<Void>from(getChannel().asyncResource(message.getLockLocation()).delete(Void.class));
    }

    @Override
    public Observable<CreateQueueResult> createQueue(QueueInfo queueInfo) {
        Builder webResourceBuilder = getResource().path(queueInfo.getPath())
                .type("application/atom+xml;type=entry;charset=utf-8");
        if ((queueInfo.getForwardTo() != null) && !queueInfo.getForwardTo().isEmpty()) {
            webResourceBuilder.header("ServiceBusSupplementaryAuthorization", queueInfo.getForwardTo());
        }

        Future<QueueInfo> queueInfoResult = webResourceBuilder.put(QueueInfo.class, queueInfo);

        Observable<CreateQueueResult> queueInfoObservable = Observable.from(queueInfoResult)
                .map(new Func1<QueueInfo, CreateQueueResult>() {
                    @Override
                    public CreateQueueResult call(QueueInfo msg) {
                        return new CreateQueueResult(msg);
                    }
                });

        return queueInfoObservable;
    }

    @Override
    public Observable<Void> deleteQueue(String queuePath) {
        return Observable.<Void>from(getResource().path(queuePath).delete(Void.class));
    }

    @Override
    public Observable<GetQueueResult> getQueue(String queuePath) {
        Future<QueueInfo> queueInfoResult = getResource().path(queuePath).get(QueueInfo.class);

        Observable<GetQueueResult> getQueueObservable = Observable.from(queueInfoResult)
                .map(new Func1<QueueInfo, GetQueueResult>() {
                    @Override
                    public GetQueueResult call(QueueInfo msg) {
                        return new GetQueueResult(msg);
                    }
                });

        return getQueueObservable;
    }

    @Override
    public Observable<ListQueuesResult> listQueues(ListQueuesOptions options) {
        Future<Feed> feed = listOptions(options, getResource().path("$Resources/Queues")).get(Feed.class);

        Observable<ListQueuesResult> listQueuesObservable = Observable.from(feed)
                .map(new Func1<Feed, ListQueuesResult>() {
                    @Override
                    public ListQueuesResult call(Feed feed) {
                        ArrayList<QueueInfo> queues = new ArrayList<QueueInfo>();
                        for (Entry entry : feed.getEntries()) {
                            queues.add(new QueueInfo(entry));
                        }
                        ListQueuesResult result = new ListQueuesResult();
                        result.setItems(queues);
                        return result;
                    }
                });

        return listQueuesObservable;

    }

    @Override
    public Observable<QueueInfo> updateQueue(QueueInfo queueInfo) {
        Builder webResourceBuilder = getResource().path(queueInfo.getPath())
                .type("application/atom+xml;type=entry;charset=utf-8").header("If-Match", "*");
        if ((queueInfo.getForwardTo() != null) && !queueInfo.getForwardTo().isEmpty()) {
            webResourceBuilder.header("ServiceBusSupplementaryAuthorization", queueInfo.getForwardTo());
        }
        return Observable.from(webResourceBuilder.put(QueueInfo.class, queueInfo));
    }

    private AsyncWebResource listOptions(AbstractListOptions<?> options, AsyncWebResource path) {
        if (options.getTop() != null) {
            path = path.queryParam("$top", options.getTop().toString());
        }
        if (options.getSkip() != null) {
            path = path.queryParam("$skip", options.getSkip().toString());
        }
        if (options.getFilter() != null) {
            path = path.queryParam("$filter", options.getFilter());
        }
        return path;
    }

    @Override
    public Observable<CreateTopicResult> createTopic(TopicInfo entry) {
        return Observable.from(getResource().path(entry.getPath()).type("application/atom+xml;type=entry;charset=utf-8")
                .put(TopicInfo.class, entry)).map(new Func1<TopicInfo, CreateTopicResult>() {
                    @Override
                    public CreateTopicResult call(TopicInfo info) {
                        return new CreateTopicResult(info);
                    }
                });
    }

    @Override
    public Observable<Void> deleteTopic(String topicPath) {
        return Observable.<Void>from(getResource().path(topicPath).delete(Void.class));
    }

    @Override
    public Observable<GetTopicResult> getTopic(String topicPath) {
        return Observable.from(getResource().path(topicPath).get(TopicInfo.class))
                .map(new Func1<TopicInfo, GetTopicResult>() {
                    @Override
                    public GetTopicResult call(TopicInfo info) {
                        return new GetTopicResult(info);
                    }
                });
    }

    @Override
    public Observable<ListTopicsResult> listTopics(ListTopicsOptions options) {
        Future<Feed> feed = listOptions(options, getResource().path("$Resources/Topics")).get(Feed.class);

        Observable<ListTopicsResult> feedObservable = Observable.from(feed).map(new Func1<Feed, ListTopicsResult>() {
            @Override
            public ListTopicsResult call(Feed feed) {
                ArrayList<TopicInfo> topics = new ArrayList<TopicInfo>();
                for (Entry entry : feed.getEntries()) {
                    topics.add(new TopicInfo(entry));
                }
                ListTopicsResult result = new ListTopicsResult();
                result.setItems(topics);
                return result;
            }
        });

        return feedObservable;
    }

    @Override
    public Observable<TopicInfo> updateTopic(TopicInfo topicInfo) {
        return Observable
                .from(getResource().path(topicInfo.getPath()).type("application/atom+xml;type=entry;charset=utf-8")
                        .header("If-Match", "*").put(TopicInfo.class, topicInfo));
    }

    @Override
    public Observable<CreateSubscriptionResult> createSubscription(String topicPath,
            SubscriptionInfo subscriptionInfo) {
        Builder webResourceBuilder = getResource().path(topicPath).path("subscriptions")
                .path(subscriptionInfo.getName()).type("application/atom+xml;type=entry;charset=utf-8");
        if ((subscriptionInfo.getForwardTo() != null) && (!subscriptionInfo.getForwardTo().isEmpty())) {
            webResourceBuilder.header("ServiceBusSupplementaryAuthorization", subscriptionInfo.getForwardTo());

        }

        return Observable.from(webResourceBuilder.put(SubscriptionInfo.class, subscriptionInfo))
                .map(new Func1<SubscriptionInfo, CreateSubscriptionResult>() {
                    @Override
                    public CreateSubscriptionResult call(SubscriptionInfo info) {
                        return new CreateSubscriptionResult(info);
                    }
                });
    }

    @Override
    public Observable<Void> deleteSubscription(String topicPath, String subscriptionName) {
        return Observable.<Void>from(getResource().path(topicPath).path("subscriptions").path(subscriptionName).delete(Void.class));
    }

    @Override
    public Observable<GetSubscriptionResult> getSubscription(String topicPath, String subscriptionName) {

        return Observable.from(
                getResource().path(topicPath).path("subscriptions").path(subscriptionName).get(SubscriptionInfo.class))
                .map(new Func1<SubscriptionInfo, GetSubscriptionResult>() {
                    @Override
                    public GetSubscriptionResult call(SubscriptionInfo info) {
                        return new GetSubscriptionResult(info);
                    }
                });
    }

    @Override
    public Observable<ListSubscriptionsResult> listSubscriptions(String topicPath, ListSubscriptionsOptions options) {
        Future<Feed> feed = listOptions(options, getResource().path(topicPath).path("subscriptions")).get(Feed.class);

        Observable<ListSubscriptionsResult> feedObservable = Observable.from(feed)
                .map(new Func1<Feed, ListSubscriptionsResult>() {
                    @Override
                    public ListSubscriptionsResult call(Feed feed) {
                        ArrayList<SubscriptionInfo> list = new ArrayList<SubscriptionInfo>();
                        for (Entry entry : feed.getEntries()) {
                            list.add(new SubscriptionInfo(entry));
                        }
                        ListSubscriptionsResult result = new ListSubscriptionsResult();
                        result.setItems(list);
                        return result;
                    }
                });

        return feedObservable;
    }

    @Override
    public Observable<SubscriptionInfo> updateSubscription(String topicName, SubscriptionInfo subscriptionInfo) {
        Builder webResourceBuilder = getResource().path(topicName).path("subscriptions")
                .path(subscriptionInfo.getName()).type("application/atom+xml;type=entry;charset=utf-8")
                .header("If-Match", "*");
        if ((subscriptionInfo.getForwardTo() != null) && !subscriptionInfo.getForwardTo().isEmpty()) {
            webResourceBuilder.header("ServiceBusSupplementaryAuthorization", subscriptionInfo.getForwardTo());
        }
        return Observable.from(webResourceBuilder.put(SubscriptionInfo.class, subscriptionInfo));
    }

    @Override
    public Observable<CreateRuleResult> createRule(String topicPath, String subscriptionName, RuleInfo rule) {

        Observable<CreateRuleResult> ret = Observable.from(getResource().path(topicPath).path("subscriptions")
                .path(subscriptionName).path("rules").path(rule.getName())
                .type("application/atom+xml;type=entry;charset=utf-8").put(RuleInfo.class, rule))
                .map(new Func1<RuleInfo, CreateRuleResult>() {
                    @Override
                    public CreateRuleResult call(RuleInfo info) {
                        return new CreateRuleResult(info);
                    }
                });

        return ret;
    }

    @Override
    public Observable<Void> deleteRule(String topicPath, String subscriptionName, String ruleName) {
        return Observable.<Void>from(getResource().path(topicPath).path("subscriptions").path(subscriptionName).path("rules")
                .path(ruleName).delete(Void.class));
    }

    @Override
    public Observable<GetRuleResult> getRule(String topicPath, String subscriptionName, String ruleName) {

        Observable<GetRuleResult> resultObservable = Observable.from(getResource().path(topicPath).path("subscriptions")
                .path(subscriptionName).path("rules").path(ruleName).get(RuleInfo.class))
                .map(new Func1<RuleInfo, GetRuleResult>() {
                    @Override
                    public GetRuleResult call(RuleInfo info) {
                        return new GetRuleResult(info);
                    }
                });

        return resultObservable;
    }

    @Override
    public Observable<ListRulesResult> listRules(String topicPath, String subscriptionName, ListRulesOptions options) {
        Future<Feed> feed = listOptions(options,
                getResource().path(topicPath).path("subscriptions").path(subscriptionName).path("rules"))
                        .get(Feed.class);

        Observable<ListRulesResult> resultObservable = Observable.from(feed).map(new Func1<Feed, ListRulesResult>() {
            @Override
            public ListRulesResult call(Feed feed) {
                ArrayList<RuleInfo> list = new ArrayList<RuleInfo>();
                for (Entry entry : feed.getEntries()) {
                    list.add(new RuleInfo(entry));
                }
                ListRulesResult result = new ListRulesResult();
                result.setItems(list);
                return result;
            }
        });

        return resultObservable;
    }

    @Override
    public Observable<ListQueuesResult> listQueues() {
        return listQueues(ListQueuesOptions.DEFAULT);
    }

    @Override
    public Observable<ListTopicsResult> listTopics() {
        return listTopics(ListTopicsOptions.DEFAULT);
    }

    @Override
    public Observable<ListSubscriptionsResult> listSubscriptions(String topicName) {
        return listSubscriptions(topicName, ListSubscriptionsOptions.DEFAULT);
    }

    @Override
    public Observable<ListRulesResult> listRules(String topicName, String subscriptionName) {
        return listRules(topicName, subscriptionName, ListRulesOptions.DEFAULT);
    }

    @Override
    public Observable<ClientResponse> renewQueueLock(String queueName, String messageId, String lockToken)
            throws ServiceException {
        Observable<ClientResponse> clientResponse = Observable.from(getResource().path(queueName).path("messages")
                .path(messageId).path(lockToken).post(ClientResponse.class, ""))
                .map(new Func1<ClientResponse, ClientResponse>() {
                    @Override
                    public ClientResponse call(ClientResponse clientResponse) {
                        PipelineHelpers.throwIfNotSuccess(clientResponse);
                        return clientResponse;
                    }
                });

        return clientResponse;
    }

    @Override
    public Observable<ClientResponse> renewSubscriptionLock(String topicName, String subscriptionName, String messageId,
            String lockToken) throws ServiceException {
        Observable<ClientResponse> clientResponse = Observable
                .from(getResource().path(topicName).path("Subscriptions").path(subscriptionName).path("messages")
                        .path(messageId).path(lockToken).post(ClientResponse.class, ""))
                .map(new Func1<ClientResponse, ClientResponse>() {
                    @Override
                    public ClientResponse call(ClientResponse clientResponse) {
                        PipelineHelpers.throwIfNotSuccess(clientResponse);
                        return clientResponse;
                    }
                });

        return clientResponse;
    }

}
