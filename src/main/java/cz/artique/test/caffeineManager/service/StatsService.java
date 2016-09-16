package cz.artique.test.caffeineManager.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class StatsService {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<HistoryEntry> getHistory(Integer userId, Integer machineId) {
		List<Object> parameters = new ArrayList<Object>(2);

		StringBuilder sb = new StringBuilder();
		sb.append(
				"SELECT u.login AS userLogin, u.id AS userId, m.name AS machineName, m.id AS machineId, c.timestamp ");
		sb.append("FROM coffee c ");
		sb.append("JOIN users u ON u.id = c.userId ");
		sb.append("JOIN machines m ON m.id = c.machineId ");
		sb.append("WHERE 1=1 ");
		if (userId != null) {
			sb.append("AND u.id = ? ");
			parameters.add(userId);
		}
		if (machineId != null) {
			sb.append("AND m.id = ? ");
			parameters.add(machineId);
		}
		sb.append("ORDER BY c.timestamp ASC");

		return jdbcTemplate.query(sb.toString(), parameters.toArray(), new RowMapper<HistoryEntry>() {
			@Override
			public HistoryEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new HistoryEntry(rs.getString("userLogin"), rs.getInt("userId"), rs.getString("machineName"),
						rs.getInt("machineId"), new DateTime(rs.getTimestamp("timestamp")));
			}
		});
	}

	private class Purchase {
		public Purchase(DateTime timestamp, int caffeine) {
			this.timestamp = timestamp;
			this.caffeine = caffeine;
		}

		private final DateTime timestamp;
		private final int caffeine;

		public DateTime getTimestamp() {
			return timestamp;
		}

		public int getCaffeine() {
			return caffeine;
		}
	}

	public List<Double> getLevels(int userId, DateTime from, Duration duration) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT c.timestamp, m.caffeine ");
		sb.append("FROM coffee c ");
		sb.append("JOIN machines m ON m.id = c.machineId ");
		sb.append("WHERE c.userId = ? ");
		sb.append("ORDER BY c.timestamp ASC");

		List<Purchase> purchases = jdbcTemplate.query(sb.toString(), new Object[] { userId },
				new RowMapper<Purchase>() {
					@Override
					public Purchase mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new Purchase(new DateTime(rs.getTimestamp("timestamp")), rs.getInt("caffeine"));
					}
				});

		return calculateLevels(purchases, from, duration);
	}

	private List<Double> calculateLevels(List<Purchase> purchases, DateTime from, Duration duration) {
		List<Double> levels = new ArrayList<Double>();
		double oneHour = 1 * 60 * 60 * 1000;
		double fiveHours = 5 * 60 * 60 * 1000;

		int i = 0;

		DateTime lastPurchase = DateTime.now(); // will be off by one hour
		double level = 0;

		// For each timestamp, calculate the level of caffeine:
		// First we calculate the caffeine which is in the bloodstream (after
		// the first hour), then we add the caffeine which is still being
		// released (in the first hour).An exponencial model assures that the
		// level diminishes by one half in five hours.
		while (!from.isAfterNow()) {
			DateTime fullEffect = from.minusHours(1);

			// exponencial part - only those in full effect
			// level contains the value at lastPurchase + 1 hour
			for (; i < purchases.size(); i++) {
				Purchase purchase = purchases.get(i);
				if (purchase.getTimestamp().isAfter(fullEffect)) {
					break;
				}

				double diff = purchase.getTimestamp().getMillis() - lastPurchase.getMillis();
				level = level * Math.pow(0.5, diff / fiveHours);

				level += purchase.getCaffeine();
				lastPurchase = purchase.getTimestamp();
			}

			double fromDiff = from.getMillis() - lastPurchase.getMillis() - oneHour;
			// effectiveLevel contains the value at from
			double effectiveLevel = level * Math.pow(0.5, fromDiff / fiveHours);

			// linear part - adding those which are not in full effect yet
			for (int j = i; j < purchases.size(); j++) {
				Purchase purchase = purchases.get(j);
				if (purchase.getTimestamp().isAfter(from)) {
					break;
				}

				double diff = from.getMillis() - purchase.getTimestamp().getMillis();
				effectiveLevel += diff / oneHour * purchase.getCaffeine();
			}

			levels.add(effectiveLevel);
			from = from.plus(duration);
		}

		return levels;
	}
}
