package dewei.unittest;

import org.apache.commons.collections4.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * 递归删除目录下所有文件个子目录下的所有文件
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return true;
        }

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    /**
     * 复制一个目录及子目录，文件到连一个目录
     * @param src
     * @param dest
     * @return
     * @throws IOException
     */
    public static boolean copyFolder(File src, File dest) throws IOException {
        if (!src.exists()) {
            return true;
        }

        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String[] files = src.list();;
            boolean ret = true;
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                ret = ret && copyFolder(srcFile, destFile);
            }

            return ret;
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();

            return true;
        }
    }

    public static boolean writeFile(String fileName, String data, boolean append) {
        FileWriter fileWriter = null;

        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            fileWriter = new FileWriter(file.getName(), append);
            fileWriter.write(data);
            fileWriter.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static List<String> readFile(String fileName) {
        List<String> tempContent = new ArrayList<>();
        BufferedReader reader = null;

        try {
            File file = new File(fileName);

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                tempContent.add(tempString);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return tempContent;
    }

    /**
     * 链接path
     * 例如pathJoin(["a", "b"]), pathJoin(["a", "/b"]), pathJoin(["a/", "/b"]) 都会产出"a/b"
     *
     * @param paths
     * @return
     */
    public static String pathJoin(List<String> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return "";
        }

        if (paths.size() == 1) {
            return paths.get(0);
        }

        String joinedPath = new File(paths.get(0), paths.get(1)).toString();
        for (int i = 2; i < paths.size(); i++) {
            joinedPath = new File(joinedPath, paths.get(i)).toString();
        }

        return joinedPath;
    }
}
