package hello.rebobatch.domain.store.writer;

import hello.rebobatch.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class StoreStagingToMainWriter {

    private final DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<Store> storeWriter() {
        return new JdbcBatchItemWriterBuilder<Store>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO store (
                            csv_id, name, category_large, category_middle, category_small,
                            road_address, latitude, longitude, created_at, updated_at, is_deleted
                        ) VALUES (
                            :csvId, :name, :categoryLarge, :categoryMiddle, :categorySmall,
                            :roadAddress, :latitude, :longitude, :createdAt, :updatedAt, :isDeleted
                        )
                        ON CONFLICT (csv_id) DO UPDATE SET
                                                name = EXCLUDED.name,
                                                category_large = EXCLUDED.category_large,
                                                category_middle = EXCLUDED.category_middle,
                                                category_small = EXCLUDED.category_small,
                                                road_address = EXCLUDED.road_address,
                                                latitude = EXCLUDED.latitude,
                                                longitude = EXCLUDED.longitude,
                                                updated_at = EXCLUDED.updated_at,
                                                is_deleted = EXCLUDED.is_deleted  
                        """)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
