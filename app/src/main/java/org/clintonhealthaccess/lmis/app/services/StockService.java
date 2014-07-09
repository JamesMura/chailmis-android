package org.clintonhealthaccess.lmis.app.services;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.Inject;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import org.clintonhealthaccess.lmis.app.LmisException;
import org.clintonhealthaccess.lmis.app.models.Commodity;
import org.clintonhealthaccess.lmis.app.models.Stock;
import org.clintonhealthaccess.lmis.app.persistence.LmisSqliteOpenHelper;

import java.sql.SQLException;

import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static com.j256.ormlite.android.apptools.OpenHelperManager.releaseHelper;
import static com.j256.ormlite.dao.DaoManager.createDao;

public class StockService {

    @Inject
    private Context context;

    public int getStockLevelFor(Commodity commodity) {
        Stock stock;
        Dao<Stock, String> stockDao;
        try {
            stockDao = initialiseDao();
            stock = stockDao.queryForId(commodity.getId());
        } catch (SQLException e) {
            throw new LmisException(e);
        } finally {
            releaseHelper();
        }

        return stock.quantity();
    }

    public Dao<Stock, String> initialiseDao() throws SQLException {
        SQLiteOpenHelper openHelper = getHelper(context, LmisSqliteOpenHelper.class);
        ConnectionSource connectionSource = new AndroidConnectionSource(openHelper);
        return createDao(connectionSource, Stock.class);
    }

}
