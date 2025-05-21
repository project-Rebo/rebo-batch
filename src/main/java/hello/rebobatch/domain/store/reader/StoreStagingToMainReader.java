package hello.rebobatch.domain.store.reader;

import hello.backend.batch.staging2.StoreStagingData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class StoreStagingToMainReader {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<StoreStagingData> stagingToMainReader(
            @Value("#{stepExecutionContext['minId']}") Long minId,
            @Value("#{stepExecutionContext['maxId']}") Long maxId) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("minId", minId);
        parameters.put("maxId", maxId);

        return new JdbcPagingItemReaderBuilder<StoreStagingData>()
                .name("stagingToMainReader")
                .dataSource(dataSource)
                .parameterValues(parameters)
                .rowMapper(new BeanPropertyRowMapper<>(StoreStagingData.class))
                .queryProvider(queryProvider().getObject())
                .pageSize(1000)
                .build();
    }

    private SqlPagingQueryProviderFactoryBean queryProvider() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT *");
        provider.setFromClause("FROM stg_store");
        provider.setWhereClause("WHERE id BETWEEN :minId AND :maxId");
        provider.setSortKey("id");
        return provider;
    }
}
