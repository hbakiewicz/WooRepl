/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Verb;
import org.scribe.model.Token;
public class OneLeggedApi10 extends DefaultApi10a  {
@Override
public String getAccessTokenEndpoint() {
    return null;
}

@Override
public String getRequestTokenEndpoint() {
    return null;
}

@Override
public String getAuthorizationUrl(Token requestToken) {
    return null;
}

@Override
public Verb getAccessTokenVerb() {
    return Verb.GET;
}

@Override
public Verb getRequestTokenVerb() {
    return Verb.GET;
}
}