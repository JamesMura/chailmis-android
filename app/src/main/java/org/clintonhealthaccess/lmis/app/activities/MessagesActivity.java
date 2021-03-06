/*
 * Copyright (c) 2014, Thoughtworks Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package org.clintonhealthaccess.lmis.app.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListView;

import com.google.inject.Inject;

import org.clintonhealthaccess.lmis.app.R;
import org.clintonhealthaccess.lmis.app.adapters.AlertsAdapter;
import org.clintonhealthaccess.lmis.app.adapters.NotificationMessageAdapter;
import org.clintonhealthaccess.lmis.app.listeners.AlertClickListener;
import org.clintonhealthaccess.lmis.app.listeners.NotificationClickListener;
import org.clintonhealthaccess.lmis.app.models.alerts.LowStockAlert;
import org.clintonhealthaccess.lmis.app.models.alerts.NotificationMessage;
import org.clintonhealthaccess.lmis.app.services.AlertsService;

import java.util.List;

import roboguice.inject.InjectView;

public class MessagesActivity extends BaseActivity {

    @InjectView(R.id.listViewNotifications)
    ListView listViewNotifications;

    @InjectView(R.id.listViewAlerts)
    ListView listViewAlerts;

    @Inject
    AlertsService alertsService;

    private View alertsListViewHeader;

    private View notificationsListViewHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.transparent);
        setContentView(R.layout.activity_messages);
        alertsListViewHeader = getLayoutInflater().inflate(R.layout.alerts_header_view, null);
        notificationsListViewHeader = getLayoutInflater().inflate(R.layout.notifications_header_view, null);
        AsyncTask<Void, Void, List<LowStockAlert>> getAlerts = new AsyncTask<Void, Void, List<LowStockAlert>>() {
            @Override
            protected List<LowStockAlert> doInBackground(Void[] params) {
                return alertsService.getEnabledLowStockAlerts();
            }

            @Override
            protected void onPostExecute(List<LowStockAlert> lowStockAlerts) {
                super.onPostExecute(lowStockAlerts);
                AlertsAdapter adapter = new AlertsAdapter(getApplicationContext(), R.layout.alert_list_item, lowStockAlerts);
                listViewAlerts.setAdapter(adapter);
                listViewAlerts.setOnItemClickListener(new AlertClickListener(adapter, MessagesActivity.this));
            }
        };
        getAlerts.execute();

        AsyncTask<Void, Void, List<? extends NotificationMessage>> getNotificationsMessageTask = new AsyncTask<Void, Void, List<? extends NotificationMessage>>() {
            @Override
            protected List<? extends NotificationMessage> doInBackground(Void... params) {
                return alertsService.getNotificationMessages();
            }

            @Override
            protected void onPostExecute(List<? extends NotificationMessage> notificationMessages) {
                NotificationMessageAdapter adapter = new NotificationMessageAdapter(getApplicationContext(), R.layout.notification_message_item, notificationMessages);
                listViewNotifications.setAdapter(adapter);
                listViewNotifications.setOnItemClickListener(new NotificationClickListener(adapter, MessagesActivity.this));
            }
        };

        getNotificationsMessageTask.execute();
    }

}
