package hello.rebobatch.domain.store.writer;

import hello.rebobatch.domain.store.entity.StoreStagingData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class StoreStagingWriterConfig {

    private final DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<StoreStagingData> storeStagingWriter() {
        return new JdbcBatchItemWriterBuilder<StoreStagingData>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                    INSERT INTO stg_store (
                        csv_id, store_name, road_address, latitude, longitude,
                        category_large, category_middle, category_small
                    ) VALUES (
                        :csvId, :storeName, :roadAddress, :latitude, :longitude,
                        :categoryLarge, :categoryMiddle, :categorySmall
                    )
                """)
                .dataSource(dataSource)
                .build();
    }
}
