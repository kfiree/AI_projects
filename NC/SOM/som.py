import numpy as np
import matplotlib.pyplot as plt


class SOM(object):

    def __init__(self, rows, cols, radius=3, learning_rate=0.5):
        self.shape = (rows, cols)
        self.initial_learning_rate = learning_rate
        self.initial_radius = radius
        self.data = None
        self.epochs = None
        # self.neurons = np.array(np.random.rand(rows, cols))  # , dtype=object)
        self.neurons = np.random.uniform(0, 1, (rows, cols, 2))

    def find_BMU(self, vector):
        min_dis_vector = np.inf
        # bmu = None

        for i, neuron in enumerate(self.neurons):
            for j, point in enumerate(neuron):
                dist = np.linalg.norm(vector - point)
                if min_dis_vector > dist:
                    min_dis_vector = dist
                    bmu = [i, j]
                # ret_index = index
        # for x in range(self.neurons.shape[0]):
        #     for y in range(self.neurons.shape[1]):
        # n = self.neurons[x, y]
        # dist = np.linalg.norm(vector - n)
        #
        # if min_dis_vector > dist:
        #     min_dis_vector = dist
        #     bmu_index = np.array([x, y])

        return bmu

    def update_neurons(self, bmu, sample_i, iter,epoches):
        curr_lr = self.initial_learning_rate * np.exp(-iter / epoches)
        for i, neuron in enumerate(self.neurons):
            for j, point in enumerate(neuron):
                dis = np.linalg.norm(np.subtract(bmu, [i, j]))
                radius = np.exp(-np.power(dis, 2) / self.initial_radius)
                self.neurons[i, j] += curr_lr * radius * (sample_i - self.neurons[i, j])

    def train(self, input, epochs=5, times=100, neurons=100, radius=.1, debug=False, fig_path=None, choose_func=None):
        self.data = input
        self.epochs = epochs
        self.plot(0, None)
        for epoch in np.arange(epochs):
            if choose_func is None:
                index = np.random.randint(0, len(self.data))
            else:
                index = choose_func(self.data)
            # np.random.shuffle(self.data)

            # for index, sample in enumerate(self.data):
            bmu = self.find_BMU(self.data[index])
            if debug and (index * (epoch + 1)) % 200 == 0:
                self.plot(index, None)  # , epoch)

            self.update_neurons(bmu, self.data[index], epoch, epochs)

            if fig_path is not None and epoch % 1000 == 0:
                self.plot_snake(epoch, fig_path)
            # if True:
            #     print(str(int(epoch / times * 100)) + '%')  # Progress percentage
        self.plot(epochs, None)

    def plot_snake(self, iter, fig_path):
        neurons = self.neurons.reshape((self.shape[0] * self.shape[1], 2,))
        fig, ax = plt.subplots()
        for i in range(self.shape[0]):
            ax.plot(self.neurons[i, :, 0], self.neurons[i, :, 1], c='r')
        for i in range(self.shape[1]):
            ax.plot(self.neurons[:, i, 0], self.neurons[:, i, 1], c='r')
        ax.scatter(neurons[:, 0], neurons[:, 1], c='r')
        ax.scatter(self.data[:, 0], self.data[:, 1], alpha=0.8)
        ax.set_title("Data size: " + str(len(self.data)) + " | "
                     + "Iteration: " + str(iter) + " | "
                     # + "Epoch :" + str(epoch) +
                                                   "lr: " + str(self.initial_learning_rate) + " | "
                     + "Radius:" + str(self.initial_radius))
        plt.savefig(
            f'{fig_path}\\{ax.get_title().replace(" ", "_").replace(":", "").replace("|", "")}.png')
        plt.show()

    def plot(self, iter, sample):  # , epoch):

        x_axis, y_axis = self.neurons[:, :, 0], self.neurons[:, :, 1]

        x_axis = x_axis.reshape((self.shape[0] * self.shape[1],))
        y_axis = y_axis.reshape((self.shape[0] * self.shape[1],))

        fig, ax = plt.subplots()
        ax.scatter(x_axis, y_axis)

        # ax.plot(xs, ys)
        ax.scatter(self.data[:, 0], self.data[:, 1], alpha=0.3)

        if sample is not None:
            ax.scatter(sample[0], sample[1], c='red')
        ax.set_title("Data size: " + str(len(self.data)) + " | "
                     + "Iteration: " + str(iter) + " | "
                     # + "Epoch :" + str(epoch) +
                                                   "\n" +
                     "lr: " + str(self.initial_learning_rate) + " | "
                     + "Radius:" + str(self.initial_radius))
        plt.show()
