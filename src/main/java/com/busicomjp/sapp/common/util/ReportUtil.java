package com.busicomjp.sapp.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.busicomjp.sapp.common.exception.SystemException;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public class ReportUtil {
	public static void openPdf(String filePath) throws IOException {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("mac") >= 0 && osName.indexOf("os") > 0) {
			List<String> commands = new ArrayList<String>();
			String command = "/usr/bin/open";
			commands.add(command);
			commands.add(filePath);

			new ProcessBuilder(commands).start();
		} else {
			Runtime.getRuntime().exec("cmd /c start " + filePath);
		}
	}

	public static boolean mergePdfFiles(List<String> filePathList, String newfilePath) {
		boolean retValue = false;
		Document document = null;
		PdfCopy destPdfFile = null;
		PdfReader reader = null;
		try {
			document = new Document(new PdfReader(filePathList.get(0)).getPageSize(1));
			destPdfFile = new PdfCopy(document, new FileOutputStream(newfilePath));
			document.open();
			for (String filePath : filePathList) {
				reader = new PdfReader(filePath);
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();

					PdfImportedPage page = destPdfFile.getImportedPage(reader, j);
					destPdfFile.addPage(page);
				}
				reader.close();

				File file = new File(filePath);
				file.delete();
				file = null;
			}
			retValue = true;
		} catch (Exception e) {
			throw new SystemException(e);
		} finally {
			if (document != null) {
				document.close();
			}
			if (destPdfFile != null) {
				destPdfFile.close();
			}
		}
		return retValue;
	}

	public static String getReportFilePath(String reportOutputPath, String reportName) {
		String filePath = null;

		String path = "./" + reportOutputPath;
		File file = new File(path);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		file = null;

		String companyName = CompanyUtil.getCompanyName();

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = dateFormatter.format(new Date());

		filePath = path + reportName + "_" + companyName + "_" + dateTime + ".pdf";

		return filePath;
	}
}
