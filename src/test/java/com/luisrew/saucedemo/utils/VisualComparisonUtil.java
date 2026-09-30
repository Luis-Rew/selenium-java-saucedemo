package com.luisrew.saucedemo.utils;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * Faz um assert visual simples: compara o screenshot atual da tela com uma
 * imagem de referência (baseline).
 *
 * Se a baseline ainda não existir, ela é criada automaticamente a partir do
 * screenshot atual, e o teste passa nessa primeira execução. Isso evita que o
 * projeto quebre em ambientes diferentes (sua máquina x GitHub Actions), onde
 * a renderização pode variar por causa de fontes, resolução ou versão do
 * navegador. Para um regression visual "de verdade" em produção, o ideal é
 * versionar a baseline e travar a versão do navegador usada para gerá-la.
 */
public final class VisualComparisonUtil {

    private static final Path BASELINE_DIR = Path.of("src/test/resources/baseline");
    private static final Path OUTPUT_DIR = Path.of("target/visual-checks");

    private VisualComparisonUtil() {
    }

    public static void assertVisualMatch(WebDriver driver, String checkName, double maxDifferencePercent) throws IOException {
        Files.createDirectories(BASELINE_DIR);
        Files.createDirectories(OUTPUT_DIR);

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage actual = ImageIO.read(screenshot);

        File actualCopy = OUTPUT_DIR.resolve(checkName + "_actual.png").toFile();
        ImageIO.write(actual, "png", actualCopy);

        File baselineFile = BASELINE_DIR.resolve(checkName + ".png").toFile();

        if (!baselineFile.exists()) {
            ImageIO.write(actual, "png", baselineFile);
            System.out.println("[visual-check] Baseline criada para '" + checkName + "'. "
                    + "Rode o teste novamente para comparar contra ela.");
            return;
        }

        BufferedImage baseline = ImageIO.read(baselineFile);

        ImageComparisonResult result = new ImageComparison(baseline, actual).compareImages();

        if (result.getImageComparisonState() != ImageComparisonState.MATCH) {
            File diffFile = OUTPUT_DIR.resolve(checkName + "_diff.png").toFile();
            ImageIO.write(result.getResult(), "png", diffFile);
        }

        double differencePercent = result.getDifferencePercent();

        assertTrue(
                String.format(
                        "A tela '%s' difere %.2f%% da baseline (máximo permitido: %.2f%%). "
                                + "Veja a imagem de diferença em: %s",
                        checkName, differencePercent, maxDifferencePercent, OUTPUT_DIR.resolve(checkName + "_diff.png")
                ),
                differencePercent <= maxDifferencePercent
        );
    }
}
