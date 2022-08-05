# import the necessary packages
import imutils
import dlib
import cv2

def convert_and_trim_bb(image, rect):
  # extract the starting and ending (x, y)-coordinates of the
  # bounding box
  startX = rect.left()
  startY = rect.top()
  endX = rect.right()
  endY = rect.bottom()
  # ensure the bounding box coordinates fall within the spatial
  # dimensions of the image
  startX = max(0, startX)
  startY = max(0, startY)
  endX = min(endX, image.shape[1])
  endY = min(endY, image.shape[0])
  # compute the width and height of the bounding box
  w = endX - startX
  h = endY - startY
  # return our bounding box coordinates
  return (startX, startY, w, h)

def execute_model(inputImageUrl, upsample):
  try:
    detector = dlib.get_frontal_face_detector()
    image = cv2.imread(inputImageUrl)
    image = imutils.resize(image, width=600)
    rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    rects = detector(rgb, upsample)
    boxes = [convert_and_trim_bb(image, r) for r in rects]

    for (x, y, w, h) in boxes:
      cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)

    extension = ''
    if inputImageUrl.find('.jpg'):
      extension = '.jpg'
    elif inputImageUrl.find('.png'):
      extension = '.png'
    elif inputImageUrl.find('.webp'):
      extension = '.webp'

    outputImageUrl = inputImageUrl.replace(extension, '_model_output' + extension)
    cv2.imwrite(outputImageUrl, image)

  except Exception as e:
    return str(e)
  else:
    # return number of detected faces
    return len(rects)