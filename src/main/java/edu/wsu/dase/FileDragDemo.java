

package edu.wsu.dase;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

@SuppressWarnings("serial")
public class FileDragDemo extends JPanel {
   private JList list = new JList();

   public FileDragDemo() {
      list.setDragEnabled(true);
      list.setTransferHandler(new FileListTransferHandler(list));

      add(new JScrollPane(list));
   }

   private static void createAndShowGui() {
      FileDragDemo mainPanel = new FileDragDemo();

      JFrame frame = new JFrame("FileDragDemo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(mainPanel);
      frame.pack();
      frame.setLocationByPlatform(true);
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGui();
         }
      });
   }
}

@SuppressWarnings("serial")
class FileListTransferHandler extends TransferHandler {
   private JList list;

   public FileListTransferHandler(JList list) {
      this.list = list;
   }

   public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
   }

   public boolean canImport(TransferSupport ts) {
      return ts.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
   }

   public boolean importData(TransferSupport ts) {
      try {
         @SuppressWarnings("rawtypes")
         List data = (List) ts.getTransferable().getTransferData(
               DataFlavor.javaFileListFlavor);
         if (data.size() < 1) {
            return false;
         }

         DefaultListModel listModel = new DefaultListModel();
         for (Object item : data) {
            File file = (File) item;
            listModel.addElement(file);
         }

         list.setModel(listModel);
         return true;

      } catch (UnsupportedFlavorException e) {
         return false;
      } catch (IOException e) {
         return false;
      }
   }
}
