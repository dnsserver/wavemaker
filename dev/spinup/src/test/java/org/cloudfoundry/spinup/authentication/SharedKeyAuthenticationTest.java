
package org.cloudfoundry.spinup.authentication;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link SharedKeyAuthentication}
 * 
 * @author Phillip Webb
 */
public class SharedKeyAuthenticationTest {

    private static final byte[] NO_BYTES = new byte[] {};

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private SharedSecret sharedSecret;

    private SharedKeyAuthentication sharedKeyAuthentication;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.sharedKeyAuthentication = new SharedKeyAuthentication(this.sharedSecret);
    }

    @Test
    public void shouldNeedSharedSecret() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("SharedSecret must not be null");
        new SharedKeyAuthentication(null);
    }

    @Test
    public void shouldNeedLoginCredentials() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Authentication Token must not be null");
        this.sharedKeyAuthentication.getTransportToken(null);
    }

    @Test
    public void shouldNeedTransportToken() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("TransportToken must not be null");
        this.sharedKeyAuthentication.getAuthenticationToken(null);
    }

    @Test
    public void shouldTransportCredentials() throws Exception {
        AuthenticationToken authenticationToken = new AuthenticationToken(NO_BYTES);
        TransportToken transportToken = new TransportToken(NO_BYTES, NO_BYTES);
        given(this.sharedSecret.encrypt(authenticationToken)).willReturn(transportToken);
        given(this.sharedSecret.decrypt(transportToken)).willReturn(authenticationToken);
        TransportToken actualTransportToken = this.sharedKeyAuthentication.getTransportToken(authenticationToken);
        AuthenticationToken actualAuthenticationToken = this.sharedKeyAuthentication.getAuthenticationToken(actualTransportToken);
        assertThat(actualAuthenticationToken, is(sameInstance(authenticationToken)));
    }
}