import math
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.preprocessing import OneHotEncoder, StandardScaler, MinMaxScaler
from sklearn.decomposition import PCA
from sklearn.impute import KNNImputer
from sklearn.metrics import accuracy_score, classification_report
from sklearn.model_selection import train_test_split, RandomizedSearchCV, cross_val_score
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, AdaBoostClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.linear_model import LogisticRegression

stroke_results = {'knn': '', 
                    'decision_trees': '',
                    'adaboost': '',
                    'logistic_regression': '',
                }

df = pd.read_csv('healthcare-dataset-stroke-data.csv')

# print(df.head())

# drop id column. Not necassery.
df.drop('id', axis=1, inplace = True)

# Reset index of dataframe
df = df.reset_index(drop = True)

# drop row with gender is 'Other'. There is only 1, so not removing is not significant.
df.drop(df.index[df['gender'] == "Other"], inplace = True)

# df.drop(['heart_disease'],axis = 1, inplace=True)

# Reset index of dataframe
df = df.reset_index(drop = True)

# chnage 'work_type' and 'smoking_status' to general values
new_work_type = {'Private' : 'Work',
                 'Self-employed' : 'Work',
                 'children': 'No Work',
                 'Govt_job' : 'Work',
                 'Never_worked' : 'No Work'
                }
# Replace old values with new
df['work_type'].replace(new_work_type, inplace = True)

# Declare dictionary of old vs new values for 'smoking_status'
new_smoking_status = {'smokes' : 'Yes',
                      'never smoked' : 'No',
                      'Unknown' : 'No',
                      'formerly smoked': 'No'
                     }
# Replace old values with new
df['smoking_status'].replace(new_smoking_status, inplace = True)

# print(df.head(5))

orig_df = df

# Categorical Columns: 'gender', 'ever_married', 'work_type', 'Residence_type', 'smoking_status'

# Initialize One Hot Encoder
one_hot_encoder = OneHotEncoder()

# Fit and Transform the columns
df_temp = one_hot_encoder.fit_transform(df[['gender', 'ever_married', 'work_type', 'Residence_type', 'smoking_status']]).toarray()

# Get newly encoded columns and concat them to the Dataframe
encodings = pd.DataFrame(columns = one_hot_encoder.get_feature_names_out(),data = df_temp)
encodings = encodings.astype(int)
df = pd.concat([df,encodings] , axis=1)

# Drop original columns from the dataset after encoding is done
df.drop(['gender', 'ever_married', 'work_type', 'Residence_type', 'smoking_status'],axis = 1, inplace=True)

# fill in missing data using k-n-n.
bmi_impute = KNNImputer(n_neighbors=71, weights='uniform') #71 is the sqrt of the data rows length

df['bmi'] = bmi_impute.fit_transform(df[['bmi']])

# # Create X and y variables for stroke
X = df.drop(['stroke'],axis=1)
y = df['stroke'].to_frame()

scaler = StandardScaler()
df = pd.DataFrame(scaler.fit_transform(X), columns=X.columns)
# print(df.head(3))

#  Plot Seaborn Heatmap to identify correlated features
fig = plt.figure(figsize=(14,12))         
sns.heatmap(X.corr(), annot = True, cmap = "Blues")
plt.show()

# PCA with 95% variance retained
pca = PCA(n_components = 0.95)
pca.fit(df)
df = pd.DataFrame(pca.transform(df))
# print(df.head(3))
# After PCA, the number of dimension is reduced to 8(from 15)

# ------ Model Training and Testing ------

# Split data into train and test sets with 80% of data as train set
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.3, random_state=0)
mm_scalar = MinMaxScaler()
X_train_scaled = mm_scalar.fit_transform(X_train)
X_test_scaled = mm_scalar.fit_transform(X_test)

# Function to fit classifier and print accuracy score
def modelAll(classifier, classifier_name, X_train = X_train_scaled, y_train = y_train,
                             X_test = X_test_scaled, y_test = y_test):
    # Fit classifier
    classifier.fit(X_train, np.ravel(y_train))
    
    # Predict testing set using the trained model
    y_pred = classifier.predict(X)
    # df.assign(heart_disease=y_pred)
    print("Model: ",type(classifier).__name__)
    print("Test Data Accuracy: %0.2f" % accuracy_score(y,y_pred))
    # print(classification_report(y_test, y_pred))
    # stroke_results.update({classifier_name: accuracy_score(y_test,y_pred)*100})
    return y_pred

# Initialize Adaboost Classifier
adaboost_classifier = AdaBoostClassifier(learning_rate=0.4)

# Initialize Decision Tree Classifier
decision_trees_classifier = DecisionTreeClassifier()

#Initialize KNN Classifier
knn_classifier = KNeighborsClassifier(n_neighbors = round(math.sqrt(X_train.size)))

#Initialize Logistic regression classifier
logistic_regression_classifier = LogisticRegression(solver='lbfgs', max_iter=100)

# # Fit Adaboost Classifier
modelAll(adaboost_classifier, 'adaboost')

# # # Fit Decision trees Classifier
modelAll(decision_trees_classifier, 'decision_trees')

# # Fit KNN Classifier
heart_disease_new =  modelAll(knn_classifier, 'knn')

# # Fir Logistic Regression Classifier
heart_disease_new =  modelAll(logistic_regression_classifier, 'logistic_regression')

print('\n\n\n')