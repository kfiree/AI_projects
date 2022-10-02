import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import accuracy_score
from sklearn.metrics import classification_report, confusion_matrix
from Knn import Knn
from tqdm import trange


def plot(data, path, y_title, top, bottom):
    data = np.array(data)
    # set width of bar
    barWidth = 0.25
    fig, ax = plt.subplots(figsize=(12, 8))

    # Set position of bar on X axis
    # br1 = np.arange(len(IT))
    br1 = np.arange(len(data))
    br2 = [x + barWidth for x in br1]
    br3 = [x + barWidth for x in br2]

    # ks = [1, 3, 5, 7, 9, 10, 11, 12, 13, 14]
    ks = [1, 3, 5, 7, 9]

    # Make the plot

    plt.bar(br1, data[:, 0], color='r', width=barWidth,
            edgecolor='grey', label='P=1')
    plt.bar(br2, data[:, 1], color='g', width=barWidth,
            edgecolor='grey', label='P=2')
    plt.bar(br3, data[:, 2], color='b', width=barWidth,
            edgecolor='grey', label='P=infinity')

    # Adding Xticks
    plt.xlabel("K value", fontweight='bold', fontsize=15)
    plt.ylabel(y_title, fontweight='bold', fontsize=15)
    plt.xticks([r + barWidth for r in range(len(ks))],
               [str(x) for x in ks])

    ax.set(ylim=[bottom, top])

    plt.legend()

    plt.savefig(path + '.png', bbox_inches='tight', dpi=150)
    plt.show()


def split_samples_and_labels(arr):
    temp = np.hsplit(arr, np.array([2, arr.shape[0]]))
    return temp[0], temp[1]


def get_data_from_file(file_path):
    # read file
    file = open(file_path, 'r')

    #  text file to array
    lines = file.readlines()
    return np.array([[float(element) for element in line.split()] for line in lines])


def eval_prediction(y_true, y_pred, k, p):
    return accuracy_score(y_true, y_pred, normalize=True)


data = get_data_from_file('two_circle.txt')

ks = [1, 3, 5, 7, 9]
ps = [1, 2, np.inf]

summary_test = np.zeros(shape=(len(ks), len(ps)))
errors_test = np.zeros(shape=(len(ks), len(ps)))
summary_train = np.zeros(shape=(len(ks), len(ps)))
errors_train = np.zeros(shape=(len(ks), len(ps)))

for _ in trange(100, desc="Training model", unit="epoch"):
    np.random.shuffle(data)

    test = data[75:]
    train = data[:75]

    x_test, y_test = split_samples_and_labels(test)
    x_train, y_train = split_samples_and_labels(train)

    train = data[:75]

    model = Knn(train)

    for i, k in enumerate(ks):
        for j, p in enumerate(ps):
            predictions = model.prediction(x_test, k, p)

            acc = eval_prediction(y_pred=predictions, y_true=y_test.reshape(75), k=k, p=p)
            errors_test[i][j] += 1 - acc
            summary_test[i][j] += acc

            predictions = model.prediction(x_train, k, p)

            acc = eval_prediction(y_pred=predictions, y_true=y_train.reshape(75), k=k, p=p)
            errors_train[i][j] += 1 - acc
            summary_train[i][j] += acc

plot(summary_test, "true_accuracy", "accuracy", 100, 95)
print("true_accuracy\n", summary_test)

plot(errors_test, "true_errors", "error", 5, 0)
print("true_errors\n", errors_test)

plot(summary_train, "empirical_accuracy", "accuracy", 100, 95)
print("empirical_accuracy\n", summary_train)

plot(errors_train, "empirical_errors", "error", 5, 0)
print("empirical_errors\n", errors_train)