package org.launchcode.stocks;

import org.launchcode.stocks.models.Startup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class StocksApplication implements CommandLineRunner {

	@Autowired
	private Startup startup;

	public static void main(String[] args) {
		SpringApplication.run(StocksApplication.class, args);
	}

	/**
	 * Callback used to run the bean.
	 *
	 * @param args incoming main method arguments
	 * @throws Exception on error
	 */
	@Override
	public void run(String... args) throws Exception {

		startup.loadSimPositions();

		Timer timer = new Timer();
		TimerTask reload = new TimerTask()
		{
			@Override
			public void run()
			{
				GregorianCalendar  today = (GregorianCalendar) GregorianCalendar.getInstance();
				int dow = today.get(GregorianCalendar.DAY_OF_WEEK);

				if (!((dow == GregorianCalendar.SATURDAY) || (dow == GregorianCalendar.SUNDAY)))
				{
					System.out.println("\n StockData reload Starting!");
					startup.reloadData();
					System.out.println("\n StockData reload Complete!");
				}
			}
		};

		Date date1 = new Date();
		int day = date1.getDate();
		int month = date1.getMonth();
		int year = date1.getYear();
		Date date2 = new Date(year, month, day, 23,59,0);
		long period = (60 * 60 * 24) * 1000; // 1 day (in milliseconds)

		//Reload the Stockdata once a day just before midnight.
		timer.scheduleAtFixedRate(reload, date2, period);
	}
}
