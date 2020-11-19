package com.example.sekerimremake.resources;

import com.example.sekerimremake.R;
import com.example.sekerimremake.models.CatalogModel;

public class CatalogItems {
    // Private constructor to prevent instantiation
    private CatalogItems() {
        throw new UnsupportedOperationException();
    }

    private static final CatalogModel BLOODGLUCOSE_MEASUREMENT= new CatalogModel("Kan Şekeri Ölçümü", R.drawable.c_bloodglucose_measurement);
    private static final CatalogModel APPLICATION_INSULIN= new CatalogModel("İnsülin Uygulaması", R.drawable.c_application_of_insulin);
    private static final CatalogModel NUTRITION= new CatalogModel("Beslenme", R.drawable.c_nutrition);
    private static final CatalogModel ORAL_ANTIDIABETIC_DRUGS= new CatalogModel("Oral Antidiyabetik İlaçlar", R.drawable.c_oral_antidiabetic_drugs);
    private static final CatalogModel EXERCISE= new CatalogModel("Egzersiz", R.drawable.c_exercise);
    private static final CatalogModel HYPOGLYCEMIA= new CatalogModel("Hipoglisemi", R.drawable.c_hypoglycemia);
    private static final CatalogModel HYPERGLYCEMIA= new CatalogModel("Hiperglisemi", R.drawable.c_hyperglycemia);
    private static final CatalogModel CHIROPODY= new CatalogModel("Ayak Bakımı", R.drawable.c_chiropody);

    private static final CatalogModel[] CATALOG_ITEMS = {BLOODGLUCOSE_MEASUREMENT, APPLICATION_INSULIN, NUTRITION, ORAL_ANTIDIABETIC_DRUGS, EXERCISE, HYPOGLYCEMIA, HYPERGLYCEMIA, CHIROPODY};

    public static CatalogModel[] getCatalogItems() {
        return CATALOG_ITEMS;
    }

    public static CatalogModel getSingleITEM(int position) {
        return CATALOG_ITEMS[position];
    }
}
