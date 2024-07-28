package com.sparos4th2.alarm.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ConsumerFactory<String, Object> pushEntityConsumerFactory() {
		JsonDeserializer<Object> deserializer = gcmPushEntityJsonDeserializer();
		return new DefaultKafkaConsumerFactory<>(
			consumerFactoryConfig(deserializer),
			new StringDeserializer(),
			deserializer);
	}

	@Bean
	public Map<String, Object> consumerFactoryConfig(JsonDeserializer<Object> deserializer) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "alarm");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		return props;
	}

	private JsonDeserializer<Object> gcmPushEntityJsonDeserializer() {
		JsonDeserializer<Object> deserializer = new JsonDeserializer<>(Object.class);
		deserializer.setRemoveTypeHeaders(false);
		deserializer.addTrustedPackages("*");
		deserializer.setUseTypeMapperForKey(true);
		return deserializer;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object>
	kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(pushEntityConsumerFactory());
		return factory;
	}
}
