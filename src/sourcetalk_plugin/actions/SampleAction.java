package sourcetalk_plugin.actions;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		String name = activePage.getActiveEditor().getEditorInput().getName();
		String code = null;
		try {
			code = getCurrentEditorContent();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        //int num= getRow(currentPane.getCaretPosition(), currentPane);
        //System.out.println(mimeType + "\n" + code);
        
        String  urlParameters = null;
        try {
            urlParameters = "conference[file_name]="
                +URLEncoder.encode(name,"UTF-8")+"&"
                +"conference[source]="
                +URLEncoder.encode(code,"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            
        }
        try {       
            String open_url = sendPost("http://sourcetalk.net/conferences",urlParameters);
            
            open_url += "/" + getRow();
            Desktop.getDesktop().browse(new URL(open_url).toURI());
        } catch (MalformedURLException ex) {
            
        } catch (IOException ex) {
            
        } catch (URISyntaxException ex) {
            
        } catch (org.eclipse.jface.text.BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
    private String sendPost(String targetURL, String urlParameters) throws MalformedURLException, IOException, URISyntaxException  {
        URL url;
        HttpURLConnection connection = null;  
        //Create connection URLEncoder.encode(urlParameters,"UTF-8") "{"conference[file_name]":"foo","conference[source]":"12432314"}"
        url = new URL(targetURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", 
             "application/x-www-form-urlencoded");

        connection.setRequestProperty("Content-Length", "" + 
                 Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");  

        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
                             connection.getOutputStream ());
                wr.writeBytes (urlParameters);
                wr.flush ();

        //Get Response	
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

              String line;
              StringBuilder response = new StringBuilder(); 
              while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
              }
              
        
        return connection.getURL().toString();
        
    }
    public static int getRow() throws org.eclipse.jface.text.BadLocationException {
    	IEditorPart editorPart =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    	ITextEditor editor = (ITextEditor) editorPart
    	        .getAdapter(ITextEditor.class);
    	IDocumentProvider provider = editor.getDocumentProvider();
    	IDocument document = provider.getDocument(editorPart
    	        .getEditorInput());
    	ITextSelection textSelection = (ITextSelection) editorPart
    	        .getSite().getSelectionProvider().getSelection();
    	int offset = textSelection.getOffset();
    	int lineNumber = document.getLineOfOffset(offset);
        return lineNumber+1;
    }
    
    public String getCurrentEditorContent() throws FileNotFoundException {
    	AbstractTextEditor part = (AbstractTextEditor) Workbench.getInstance()
    	        .getActiveWorkbenchWindow().getActivePage().getActiveEditor()
    	        .getAdapter(AbstractTextEditor.class);
    	String content = null;
    	if (part != null) {

    	    IDocument document = part.getDocumentProvider().getDocument(
    	            part.getEditorInput());

    	    content = document.get();

    	    //do something with the text
    	}
        return content;
    }
}