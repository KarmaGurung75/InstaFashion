const router = require("express").Router();
const productController = require("../controllers/productController.js");
const auth = require("../middleware/auth.js");

router.post("/", auth.verifyUser, productController.add_product);

router.get("/archive", auth.verifyUser, productController.viewArchive);

router.get("/:id", auth.verifyUser, productController.get_product);

router.patch(
  "/preparation/:id",
  auth.verifyUser,
  productController.update_preparation
);

router.patch(
  "/ingredients/:id",
  auth.verifyUser,
  productController.update_ingredients
);

router.patch(
  "/direction/:id",
  auth.verifyUser,
  productController.update_direction
);

router.patch("/hashtag/:id", auth.verifyUser, productController.update_hashtag);

router.delete(
  "/discard/:id",
  auth.verifyUser,
  productController.discard_product
);

router.post("/ok/:id", auth.verifyUser, productController.post_product);

router.post("/share/:id", auth.verifyUser, productController.share_product);

router.patch(
  "/no-image/:id",
  auth.verifyUser,
  productController.update_product_without_image
);

router.post("/archive/:id", auth.verifyUser, productController.archive_product);

router.post("/ok/:id", auth.verifyUser, productController.post_product);

router.delete("/:id", auth.verifyUser, productController.delete_product);

module.exports = router;
