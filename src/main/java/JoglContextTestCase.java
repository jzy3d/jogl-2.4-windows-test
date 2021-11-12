import java.awt.BorderLayout;
import java.awt.BufferCapabilities;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.image.ColorModel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


import com.google.common.base.Strings;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

// See instructions in readme.md
//
// 2D flags reminder : https://docs.oracle.com/javase/8/docs/technotes/guides/2d/flags.html
public class JoglContextTestCase {
	private static JFrame mainFrame = null;

	private static void buildSceneAndCheckConfigurations() {
		System.out.println("sun.java2d.opengl:" + System.getProperty("sun.java2d.opengl"));
		System.out.println("sun.java2d.noddraw:" + System.getProperty("sun.java2d.noddraw"));
		
		GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphDevsOrig = graphEnv.getScreenDevices();

		GraphicsConfiguration gcDef = graphDevsOrig[0].getDefaultConfiguration();

		System.out.println("--------------------");
		System.out.println("Default graphics configuration");
		print(gcDef, 1);
		System.out.println("--------------------");
		
		// *************************************************************************
		// NOTE: if I comment the following loop, which apparently does NOTHING
		// (except calling .getGraphicsConfigurations()), this test case
		// returns "GL version: 4.6" on my PC, while with the following loop
		// uncommented, it returns "GL version: 1.1" --> this would cause VTK
		// to CRASH!
		// The single call responsible for the "GL 1.1" issue, for some reason,
		// seems to be 'graphDevsOrig[i].getConfigurations();'
		// *************************************************************************
		for (int i = 0; i < graphDevsOrig.length; i++) {
			System.out.println("Screen device # " + i + ": " + graphDevsOrig[i].getIDstring());
			GraphicsConfiguration[] graphConfs = graphDevsOrig[i].getConfigurations();

			for (int j = 0; j < graphConfs.length; j++) {
				GraphicsConfiguration gc = graphConfs[j];

				System.out.println(" Screen device # " + i + ", configuration # " + j + ":");

				print(gc, 1);
			}
			System.out.println("----");
		}

		buildScene(gcDef);
	}

	private static void print(GraphicsConfiguration gc, int tabs) {
		String tab = Strings.repeat("\t", tabs);
		
		ColorModel cm = gc.getColorModel();
		System.out.println(tab + "ColorModel   : " + gc.getColorModel());

		ImageCapabilities ic = gc.getImageCapabilities();
		System.out.println(tab + "Img Caps     : " + str(ic));
		System.out.println(tab + "Bounds       : " + gc.getBounds());
		System.out.println(tab + "Translucency : " + gc.isTranslucencyCapable());

		BufferCapabilities bc = gc.getBufferCapabilities();
		System.out.println(tab + "Buffer caps / Back       : " + str(bc.getBackBufferCapabilities()));
		System.out.println(tab + "Buffer caps / Front      : " + str(bc.getFrontBufferCapabilities()));
		System.out.println(tab + "Buffer caps / FullScreen : " + bc.isFullScreenRequired());
	}

	private static String str(ImageCapabilities ic) {
		return "accelerated: " + ic.isAccelerated() + " volatile: " + ic.isTrueVolatile();
	}

	private static void buildScene(GraphicsConfiguration graphConf) {
		GLCanvas glCanvas = new GLCanvas(new GLCapabilities(GLProfile.getMaximum(true)));
		glCanvas.addGLEventListener(new GLEventListener() {
			@Override
			public void init(final GLAutoDrawable drawable) {
				System.out.println("* Context GL version: " + drawable.getContext().getGLVersion());

				print(glCanvas.getGraphicsConfiguration(), 1);
			}

			@Override
			public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width,
					final int height) {
			}

			@Override
			public void display(final GLAutoDrawable drawable) {
			}

			@Override
			public void dispose(final GLAutoDrawable drawable) {
			}
		});

		// UI part
		mainFrame = new JFrame("SimpleVTK", graphConf);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout());

		mainFrame.setSize(1000, 600);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mainFrame.getContentPane().add(glCanvas, BorderLayout.CENTER);
		glCanvas.requestFocus();

		System.out.println("NewFrame created in thread [" + Thread.currentThread().getId() + "], isEDT: "
				+ SwingUtilities.isEventDispatchThread());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildSceneAndCheckConfigurations();
			}
		});
	}
}