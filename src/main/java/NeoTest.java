import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetMapRequest;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.ows.ServiceException;
import org.xml.sax.SAXException;

public class NeoTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        URL url = null;
        try {
            // url = new URL(
            // "http://www2.dmsolutions.ca/cgi-bin/mswms_gmap?VERSION=1.1.0&REQUEST=GetCapabilities");
            url = new URL(
                    "http://neowms.sci.gsfc.nasa.gov/wms/wms?version=1.3.0&service=WMS&request=GetCapabilities"
                    // "http://neowms.sci.gsfc.nasa.gov/wms/wms?version=1.1.1&service=WMS&request=GetCapabilities"
                    );
        } catch (MalformedURLException e) {
            // will not happen
        }

        WebMapServer wms = null;
        try {
            wms = new WebMapServer(url);
        } catch (IOException e) {
            // There was an error communicating with the server
            // For example, the server is down
        } catch (ServiceException e) {
            // The server returned a ServiceException (unusual in this case)
        } catch (SAXException e) {
            // Unable to parse the response from the server
            // For example, the capabilities it returned was not valid
        }
        WMSCapabilities capabilities = wms.getCapabilities();
        List<String> formats = wms.getCapabilities().getRequest().getGetMap().getFormats();
        for (String f : formats) {
            System.out.println(f);
        }
        GetMapRequest request = wms.createGetMapRequest();
        request.setFormat("MOD14A1_M_FIRE");
        request.setFormat("image/png");
        request.setDimensions("583", "420"); // sets the dimensions of the image
                                             // to be returned from the server
        request.setTransparent(true);
        org.geotools.data.wms.WMS1_3_0.GetMapRequest request130 = (org.geotools.data.wms.WMS1_3_0.GetMapRequest) request;
        // request.setSRS("EPSG:4326"); v1.1.1
        request.setTime("2011-11-01");
        request.setSRS("CRS:84"); // v1.3.0
        request.setBBox("-180.0,-90.0,180.0,90.0");

        for (Layer layer : WMSUtils.getNamedLayers(capabilities)) {
            // System.out.println(layer.getName());
            // test
            if (layer.getName().equals("MCD43C3_M_BSA")) {
                System.out.println(layer.getName());
                request.addLayer(layer);
            }
        }

        GetMapResponse response;
        BufferedImage image = null;
        try {
            response = (GetMapResponse) wms.issueRequest(request);
            image = ImageIO.read(response.getInputStream());
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (image == null) {
            return;
        }

        try {
            ImageIO.write(image, "png", new File("/workspace/tmp/earth.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
