/**
 * Copyright (C) 2016 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package db.migration.mysql;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.google.common.collect.ImmutableMap;

import fr.ymanvieu.trading.common.provider.entity.PairEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class V2_4_0_2__google_to_yahoo_pairs extends BaseJavaMigration {
	
	public static final Pattern GOOGLE_CODE_PATTERN = Pattern.compile("(?>(?<exch>\\w+):)?(?<symbol>\\w+)");

	@Override
	public void migrate(Context context) throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true));
		
		List<PairEntity> result = jdbcTemplate.query("select * from pair where provider_code='GOOGLE'", new BeanPropertyRowMapper<>(PairEntity.class));

		Map<String, String> googleToYahooExchanges = ImmutableMap.<String, String>builder() //
				.put("EPA", "PA") //
				.put("KRX", "KS") //
				.put("TPE", "TW") //
				.put("ETR","DE") //
				.put("AMS","AS") //
				.put("LON","L") //
				.put("BIT","MI") //
				.put("HKG","HK") //
				.put("NASDAQ","") //
				.build();
		
		result.forEach(pe -> {
			//log.info("{}", pe);
			
			Matcher m = GOOGLE_CODE_PATTERN.matcher(pe.getSymbol());
			
			if(!m.matches()) {
				log.warn("no match for {}", pe.getSymbol());
			} else {

				String foundExchange = m.group("exch");
				
				if("TYO".equals(foundExchange)) {
					return;
				}
				
				String exchange = googleToYahooExchanges.getOrDefault(foundExchange, "");
				String symbol = m.group("symbol");
				
				final String newCode;
				
				if(exchange.isEmpty()) {
					newCode = symbol;
				} else {
					newCode = symbol + "." + exchange;
				}				
				
				//log.info("{} (exch: {}, symbol: {}) => {}", pe.getSymbol(), foundExchange, symbol, newCode);

				jdbcTemplate.update("update pair set symbol=?,provider_code=? where id=?", newCode, "YAHOO", pe.getId());
			}
		});
	}
}