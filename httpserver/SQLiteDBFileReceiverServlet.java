import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class SQLiteDBFileReceiverServlet extends HttpServlet {
    private static final long serialVersionUID = -32084194456L;

    @Override
    public void init() throws ServletException {
    	super.init();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException
	{
        try {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            Iterator iterator = upload.parseRequest(request).iterator();
            while (iterator.hasNext())
            {
                FileItem item = (FileItem) iterator.next();
                if (!item.isFormField()) {
                    File path = new File(getServletContext().getRealPath("/") + "/uploads");
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    item.write(new File(path + "/" + item.getName()));
                }
            }
        	response.setStatus(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
    	super.destroy();
    }
}
