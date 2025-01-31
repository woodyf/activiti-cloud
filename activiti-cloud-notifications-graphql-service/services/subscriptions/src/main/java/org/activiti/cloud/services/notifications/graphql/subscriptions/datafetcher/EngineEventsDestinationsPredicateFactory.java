/*
 * Copyright 2017-2020 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cloud.services.notifications.graphql.subscriptions.datafetcher;

import java.util.List;
import java.util.function.Predicate;

import graphql.schema.DataFetchingEnvironment;
import org.activiti.cloud.services.notifications.graphql.events.RoutingKeyResolver;
import org.activiti.cloud.services.notifications.graphql.events.model.EngineEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

public class EngineEventsDestinationsPredicateFactory implements EngineEventsPredicateFactory {

    private static Logger logger = LoggerFactory.getLogger(EngineEventsDestinationsPredicateFactory.class);

    private final RoutingKeyResolver routingKeyResolver;

    private DataFetcherDestinationResolver destinationResolver = new AntPathDestinationResolver();
    private AntPathMatcher pathMatcher = new AntPathMatcher(".");

    public EngineEventsDestinationsPredicateFactory(RoutingKeyResolver routingKeyResolver) {
        this.routingKeyResolver = routingKeyResolver;
    }

    // filter events that do not match subscription arguments
    @Override
    public Predicate<? super EngineEvent> getPredicate(DataFetchingEnvironment environment) {
        List<String> destinations = destinationResolver.resolveDestinations(environment);

        logger.info("Resolved destinations {} for environment: {}", destinations, environment);

        return (engineEvent) -> {
            String routingKey = routingKeyResolver.resolveRoutingKey(engineEvent);

            logger.debug("Resolved routing key {} for {}", routingKey, engineEvent);

            return destinations.stream()
                               .anyMatch(pattern -> pathMatcher.match(pattern, routingKey));
        };
    }

    public EngineEventsDestinationsPredicateFactory destinationResolver(DataFetcherDestinationResolver destinationResolver) {
        this.destinationResolver = destinationResolver;

        return this;
    }

    public EngineEventsDestinationsPredicateFactory pathMatcher(AntPathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;

        return this;
    }
}
