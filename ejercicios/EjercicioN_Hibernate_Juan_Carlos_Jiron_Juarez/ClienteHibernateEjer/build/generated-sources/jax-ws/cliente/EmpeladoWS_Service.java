
package cliente;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.11-b150120.1832
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "EmpeladoWS", targetNamespace = "http://web.ipn.com/", wsdlLocation = "http://localhost:8080/ejerHibernete/EmpeladoWS?wsdl")
public class EmpeladoWS_Service
    extends Service
{

    private final static URL EMPELADOWS_WSDL_LOCATION;
    private final static WebServiceException EMPELADOWS_EXCEPTION;
    private final static QName EMPELADOWS_QNAME = new QName("http://web.ipn.com/", "EmpeladoWS");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/ejerHibernete/EmpeladoWS?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        EMPELADOWS_WSDL_LOCATION = url;
        EMPELADOWS_EXCEPTION = e;
    }

    public EmpeladoWS_Service() {
        super(__getWsdlLocation(), EMPELADOWS_QNAME);
    }

    public EmpeladoWS_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), EMPELADOWS_QNAME, features);
    }

    public EmpeladoWS_Service(URL wsdlLocation) {
        super(wsdlLocation, EMPELADOWS_QNAME);
    }

    public EmpeladoWS_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, EMPELADOWS_QNAME, features);
    }

    public EmpeladoWS_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EmpeladoWS_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns EmpeladoWS
     */
    @WebEndpoint(name = "EmpeladoWSPort")
    public EmpeladoWS getEmpeladoWSPort() {
        return super.getPort(new QName("http://web.ipn.com/", "EmpeladoWSPort"), EmpeladoWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns EmpeladoWS
     */
    @WebEndpoint(name = "EmpeladoWSPort")
    public EmpeladoWS getEmpeladoWSPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://web.ipn.com/", "EmpeladoWSPort"), EmpeladoWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (EMPELADOWS_EXCEPTION!= null) {
            throw EMPELADOWS_EXCEPTION;
        }
        return EMPELADOWS_WSDL_LOCATION;
    }

}