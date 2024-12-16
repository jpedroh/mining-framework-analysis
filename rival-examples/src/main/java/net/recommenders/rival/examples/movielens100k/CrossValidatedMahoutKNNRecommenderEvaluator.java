package net.recommenders.rival.examples.movielens100k;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.recommenders.rival.core.DataModel;
import net.recommenders.rival.core.Parser;
import net.recommenders.rival.core.SimpleParser;
import net.recommenders.rival.evaluation.metric.error.RMSE;
import net.recommenders.rival.evaluation.metric.ranking.MAP;
import net.recommenders.rival.evaluation.metric.ranking.NDCG;
import net.recommenders.rival.evaluation.metric.ranking.Precision;
import net.recommenders.rival.evaluation.strategy.EvaluationStrategy;
import net.recommenders.rival.recommend.frameworks.RecommenderIO;
import net.recommenders.rival.recommend.frameworks.mahout.GenericRecommenderBuilder;
import net.recommenders.rival.recommend.frameworks.mahout.exceptions.RecommenderException;
import net.recommenders.rival.split.parser.MovielensParser;
import net.recommenders.rival.split.splitter.CrossValidationSplitter;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;


/**
 *
 *
 * @author <a href="http://github.com/alansaid">Alan</a>
 */
public class CrossValidatedMahoutKNNRecommenderEvaluator {
    public static void main(String[] args) {
        String url = "http://files.grouplens.org/datasets/movielens/ml-100k.zip";
        int nFolds = 5;
        prepareSplits(url, nFolds);
        recommend(nFolds);
        prepareStrategy(nFolds);
        evaluate(nFolds);
    }

    public static void prepareSplits(String url, int nFolds) {
        DataDownloader dd = new DataDownloader(url);
        dd.downloadAndUnzip();
        boolean perUser = true;
        long seed = 2048;
        Parser parser = new MovielensParser();
        DataModel<Long, Long> data = null;
        try {
            data = parser.parseData(new File("data/ml-100k/u.data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataModel<Long, Long>[] splits = new CrossValidationSplitter(nFolds, perUser, seed).split(data);
        String path = "data/model/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (int i = 0; i < (splits.length / 2); i++) {
            DataModel<Long, Long> training = splits[2 * i];
            DataModel<Long, Long> test = splits[(2 * i) + 1];
            String trainingFile = ((path + "train.") + i) + ".csv";
            String testFile = ((path + "test.") + i) + ".csv";
            System.out.println("train: " + trainingFile);
            System.out.println("test: " + testFile);
            boolean overwrite = true;
            try {
                training.saveDataModel(trainingFile, overwrite);
                test.saveDataModel(testFile, overwrite);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recommend(int nFolds) {
        for (int i = 0; i < nFolds; i++) {
            org.apache.mahout.cf.taste.model.DataModel trainModel = null;
            org.apache.mahout.cf.taste.model.DataModel testModel = null;
            try {
                trainModel = new FileDataModel(new File("data/model/train." + i + ".csv"));
                testModel = new FileDataModel(new File("data/model/test." + i + ".csv"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            GenericRecommenderBuilder grb = new GenericRecommenderBuilder();
            String recommenderClass = "org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender";
            String similarityClass = "org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity";
            int neighborhoodSize = 50;
            Recommender recommender = null;
            try {
                recommender = grb.buildRecommender(trainModel, recommenderClass, similarityClass, neighborhoodSize);
            } catch (TasteException e) {
                e.printStackTrace();
            } catch (RecommenderException e) {
                e.printStackTrace();
            }

            String path = "data/recommendations";
            String fileName = "recs." + i + ".csv";

            LongPrimitiveIterator users = null;
            try {
                users = testModel.getUserIDs();
                while (users.hasNext()) {
                    long u = users.nextLong();
                    List<RecommendedItem> items = recommender.recommend(u, trainModel.getNumItems());
                    RecommenderIO.writeData(u, items, path, fileName);
                }
            } catch (TasteException e) {
                e.printStackTrace();
            }

        }
    }

    public static void prepareStrategy(int nFolds) {
        for (int i = 0; i < nFolds; i++) {
            File trainingFile = new File(("data/model/train." + i) + ".csv");
            File testFile = new File(("data/model/test." + i) + ".csv");
            File recFile = new File(("data/recommendations/recs." + i) + ".csv");
            DataModel<Long, Long> trainingModel = null;
            DataModel<Long, Long> testModel = null;
            DataModel<Long, Long> recModel = null;
            try {
                trainingModel = new SimpleParser().parseData(trainingFile);
                testModel = new SimpleParser().parseData(testFile);
                recModel = new SimpleParser().parseData(recFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            EvaluationStrategy.OUTPUT_FORMAT format = EvaluationStrategy.OUTPUT_FORMAT.SIMPLE;
            Double threshold = 2.0;
            String strategyClassName = "net.recommenders.rival.evaluation.strategy.UserTest";
            EvaluationStrategy<Long, Long> strategy = null;
            try {
                Object strategyObj = Class.forName(strategyClassName).getConstructor(DataModel.class, DataModel.class, double.class).newInstance(trainingModel, testModel, threshold);
                if (strategyObj instanceof EvaluationStrategy) {
                    @SuppressWarnings("unchecked")
                    EvaluationStrategy<Long, Long> strategyTemp = ((EvaluationStrategy<Long, Long>) (strategyObj));
                    strategy = strategyTemp;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (java.lang.NoSuchMethodException e) {
                e.printStackTrace();
            } catch (java.lang.ClassNotFoundException e) {
                e.printStackTrace();
            }
            DataModel<Long, Long> modelToEval = new DataModel<Long, Long>();
            for (Long user : recModel.getUsers()) {
                for (Long item : strategy.getCandidateItemsToRank(user)) {
                    if (recModel.getUserItemPreferences().get(user).containsKey(item)) {
                        modelToEval.addPreference(user, item, recModel.getUserItemPreferences().get(user).get(item));
                    }
                }
            }
            try {
                modelToEval.saveDataModel(("data/model/strategymodel." + i) + ".csv", true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void evaluate(int nFolds){
        double ndcgRes = 0.0;
        double precisionRes = 0.0;
        for (int i = 0; i < nFolds; i++) {
            File testFile = new File("data/model/test." + i + ".csv");
            File recFile = new File("data/recommendations/recs." + i + ".csv");
            DataModel<Long, Long> testModel = null;
            DataModel<Long, Long> recModel = null;
            try {
                testModel = new SimpleParser().parseData(testFile);
                recModel = new SimpleParser().parseData(recFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            NDCG ndcg = new NDCG(recModel, testModel, new int[]{10});
            ndcg.compute();
            ndcgRes += ndcg.getValueAt(10);
    //            System.out.println("NDCG@10: " + ndcg.getValueAt(10));

    //            RMSE rmse = new RMSE(recModel, testModel);
    //            rmse.compute();
    //            System.out.println(rmse.getValue());
        }
        System.out.println("NDCG@10: " + ndcgRes/nFolds);
    }
}