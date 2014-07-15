package org.clintonhealthaccess.lmis.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    private StubAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new StubAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
