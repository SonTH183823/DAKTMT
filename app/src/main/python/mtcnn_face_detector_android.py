from matplotlib import pyplot
from matplotlib.patches import Rectangle
from mtcnn.mtcnn import MTCNN
import cv2

# draw an image with detected objects
def draw_image_with_boxes(filename, result_list):
	# load the image
	data = pyplot.imread(filename)
	# plot the image
	pyplot.imshow(data)
	# get the context for drawing boxes
	ax = pyplot.gca()
	# plot each box
	for result in result_list:
		# get coordinates
		x, y, width, height = result['box']
		# create the shape
		rect = Rectangle((x, y), width, height, fill=False, color='green')
		# draw the box
		ax.add_patch(rect)
	# show the plot
# 	pyplot.show()

def execute_model(inputImageUrl):
  try:
    # load image from file
    pixels = pyplot.imread(inputImageUrl)
    pyplot.axis('off')
    # create the detector, using default weights
    detector = MTCNN()
    # detect faces in the image
    faces = detector.detect_faces(pixels)
    # display faces on the original image
    draw_image_with_boxes(inputImageUrl, faces)

    extension = ''
    if inputImageUrl.find('.jpg'):
      extension = '.jpg'
    elif inputImageUrl.find('.png'):
      extension = '.png'
    elif inputImageUrl.find('.webp'):
      extension = '.webp'

    outputImageUrl = inputImageUrl.replace(extension, '_model_output' + extension)
    pyplot.savefig(outputImageUrl)

  except Exception as e:
    return str(e)
  else:
    # return number of detected faces
    return len(faces)