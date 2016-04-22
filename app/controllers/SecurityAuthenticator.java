package controllers;

import commons.SessionKey;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class SecurityAuthenticator extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get(SessionKey.USER_ID.name());
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Application.index());
    }
}