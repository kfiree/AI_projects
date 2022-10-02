import numpy as np
from matplotlib import pyplot as plt
from numpy import random
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay
from sklearn.neural_network import MLPClassifier
from sklearn.neural_network._base import ACTIVATIONS

from Adaline import Part, Adaline

DATA_SIZE = 1000


def paintRed(msg): return "\033[91m {}\033[00m".format(msg)


def paintGreen(msg): return "\033[92m {}\033[00m".format(msg)


def generateSamples(data_size=DATA_SIZE, debug=False, part=Part.A):
    np.random.seed(1)
    x_int = np.random.randint(-100, 99, size=(data_size, 2))
    if part == Part.B:
        i = np.random.randint(0, 199)
        x_int[i][0] = 1
        x_int[i][1] = 1

    x_frac = np.round(np.random.rand(data_size, 2), 2)
    x = x_int + x_frac

    y = np.empty(shape=[data_size])
    if part == Part.A:
        for i in range(data_size):
            y[i] = 1 if x[i][1] > 1 else -1

    elif part == Part.B:
        X1 = x[:, 0] ** 2
        X2 = x[:, 1] ** 2
        sum = np.add(X1, X2)
        for i in range(data_size):
            y[i] = 1 if sum[i] >= 4 and sum[i] <= 9 else -1

    if debug:
        print("x: \n", x, "\n")
        print("y: \n", y, "\n")
        # print("data: \n", data, "\n")
    X = x.astype(np.float64)  # test
    y = y.astype(np.float64)  #
    return X, y


def forward_prop(clf, input, layers, for_adaline=False):
    # if layers is None or layers == 0:
    #     layers = clf.n_layers_
    # create first layer without any activation, cause input layer
    data = input
    # get the activation function, as writen in
    # https://github.com/scikit-learn/scikit-learn/blob/7b136e92acf49d46251479b75c88cba632de1937/sklearn/neural_network/_base.py#L98
    activation_function = ACTIVATIONS[clf.activation]

    # Forward propagate
    for i in range(layers - 1):
        weight, bias = clf.coefs_[i], clf.intercepts_[i]
        data = np.matmul(data, weight) + bias
        if i != layers - 2:
            activation_function(data)

    # if not at the end of neural-network then re-label the output
    if data.shape[1] > 1:
        if for_adaline:
            return data
        return np.array([clf._label_binarizer.inverse_transform(data[:, i]) for i in range(data.shape[1])])
    activation_function(data)
    return clf._label_binarizer.inverse_transform(data)


def plot_neuron(X, layer_i, index_neuron, neuron):
    plt.scatter(x=X[neuron == -1, 0], y=X[neuron == -1, 1],
                c='green',
                marker='s', label=-1.0)

    plt.scatter(x=X[neuron == 1, 0], y=X[neuron == 1, 1],
                c='red',
                label=1.0)

    plt.legend(loc='upper left')
    plt.title("Layer: " + str(layer_i) + " Neuron: " + str(index_neuron))
    # plt.savefig(f'layer_{str(layer_i)}_neuron_{str(index_neuron)}.png')
    plt.show()


def plot_layers(clf, X, index_layer, layer, output=False):
    index_neuron = 0
    for neuron in layer:
        plot_neuron(X, index_layer, index_neuron + 1, neuron)
        index_neuron += 1

    if output:
        # presentation of output layers
        output_layer = forward_prop(clf, X, clf.n_layers_)
        print("output layer")
        plot_neuron(X, index_layer + 1, 1, output_layer)


def calculateResults(_x, _y, model, debug=False, part=Part.A):
    false_negative, false_positive, true_negative, true_positive = model.test(_x, _y)

    print("                PART ", part.name,
          "\n               --------")
    if debug:
        print("weights: ", model.weights)

    # Confusion Matrix

    print("\n           Confusion Matrix:",
          "\n         ===================\n",
          paintGreen("True positive:"), true_positive, paintRed("False positive:"), false_positive, "\n",
          paintRed("False_negative:"), false_negative, paintGreen("True negative:"), true_negative)

    missed_class = false_positive + false_negative
    score = (DATA_SIZE - missed_class) / DATA_SIZE
    print("\n         \033[1mAccuracy:\033[0m ", score * 100, "% success")

    return model


def showResults(_x, _y, model, p=Part.A):
    model = calculateResults(_x, _y, model, part=p, debug=False)
    b = -model.weights[0] / model.weights[2]
    m = -model.weights[1] / model.weights[2]

    x = [-100, 100]
    y = [-100 * m + b, 100 * m + b]
    plt.plot(x, y)
    neg_x, neg_y, pos_x, pos_y = model.splitAsShouldBe(_x, _y)
    plt.scatter(pos_x, pos_y, label="stars", color="green", marker="*", s=30)
    plt.scatter(neg_x, neg_y, label="stars", color="red", marker="*", s=30)
    plt.xlabel('x - axis')
    plt.ylabel('y - axis')

    title = "PART " + p.name + ": Adaline Algorithm\n" + str(model.data_size) + " samples " + str(
        model.learning_rate) + " learning rate"
    plt.title(title)

    plt.xlim(_x[:, 0].min(), _x[:, 0].max())
    plt.ylim(_x[:, 1].min(), _x[:, 1].max())
    # plt.savefig(
    #     f'PART_{p.name}_adaline_algorithm_{str(model.data_size)}_samples_{str(model.learning_rate)}_learning_rate.png')
    plt.show()

    plt.plot(model.costs, '-b', label="loss")
    plt.xlabel("n epoch")
    plt.legend(loc='upper left')
    title = "PART " + p.name + ": Loss progrees\n" + str(model.data_size) + " samples " + str(
        model.learning_rate) + " learning rate"
    plt.title(title)

    print("final weights: ", model.weights)

    # plt.plot(model.costs)
    # plt.savefig(
    #     f'PART_{p.name}_loss_progrees_{str(model.data_size)}_samples_{str(model.learning_rate)}_learning_rate.png')
    plt.show()


def with_adaline(clf, X, y, X2, y2, part):
    """
    prepares the data to run in adaline
    and prints the results of the run in adaline with the transformed data
    :param clf: the plot_classifier to run the data through
    :param X:  as
    :param y:  sad
    :param X2:  asd
    :param y2:  asd
    :param part:  asd
    :return:
    """
    X_clf_output = []
    for layer_index in range(1, clf.n_layers_):
        layer_i = forward_prop(clf, X, layer_index)
        if layer_index == clf.n_layers_ - 1:
            X_clf_output = forward_prop(clf, X, layer_index, for_adaline=True)
        plot_layers(clf, X, layer_index - 1, layer_i)
    X_clf_output = np.array([X_clf_output[0], X_clf_output[1]]).T
    model = Adaline(data_size=DATA_SIZE, part=Part.A)
    model.train(X_clf_output, y, debug=False)
    # false_negative, false_positive, true_negative, true_positive = model.test(X2, y2)
    showResults(X2, y2, model, part)


def plot_classifier(clf, X, y):
    """
    plots the classifier
    :param clf: classifier
    :param X: data classified
    :param y: labels of X
    :return:
    """
    plot_neuron(X, 0, 1, y)
    for index in range(2, clf.n_layers_):
        layer_i = forward_prop(clf, X, index)
        if index == clf.n_layers_ - 1:
            plot_layers(clf, X, index - 1, layer_i, True)
        else:
            plot_layers(clf, X, index - 1, layer_i)
    print(clf.score(X, y))


if __name__ == '__main__':
    X1, y1 = generateSamples()
    X12, y12 = generateSamples()
    X_c, y_c = generateSamples(DATA_SIZE, False, Part.B)
    X2_c, y2_c = generateSamples(DATA_SIZE, False, Part.B)
    # Part A
    clf_a = MLPClassifier(activation='logistic', learning_rate_init=0.1,
                          hidden_layer_sizes=(8, 2), random_state=1, max_iter=150)
    clf_a.fit(X, y)

    plot_classifier(clf_a, X, y)
    cm = confusion_matrix(y2, clf_a.predict(X2))
    disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=clf_a.classes_)
    disp.plot()

    plt.show()

    # # Part B
    clf_b = MLPClassifier(activation='logistic', learning_rate_init=0.1,
                          hidden_layer_sizes=(8, 2,), random_state=1, max_iter=150)
    clf_b.fit(X_c, y_c)
    plot_classifier(clf_b, X_c, y_c)
    cm = confusion_matrix(y2_c, clf_b.predict(X2_c))
    disp = ConfusionMatrixDisplay(confusion_matrix=cm,display_labels = clf_b.classes_)
    disp.plot()


    plt.show()

    # with adaline
    with_adaline(clf_a, X1, y1, X12, y12, Part.A)
    with_adaline(clf_b, X_c, y_c, X2_c, y2_c, Part.B)
