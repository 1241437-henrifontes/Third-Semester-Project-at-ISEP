package Controllers;

import Repositories.TreeRepository;
import Model.Trees.KDtree;

/**
 * Controller responsible for building and inspecting the KD-tree
 * used for proximity searches.
 */
public class KDtreeBuildController {
    private final TreeRepository repo = TreeRepository.getInstance();

    /**
     * Builds the KD-tree if the prerequisite AVL trees are available.
     * Prints feedback to the console regarding the operation status.
     */
    public void buildKDTree() {
        if (repo.getLatitudeTree().isEmpty() || repo.getLongitudeTree().isEmpty()) {
            System.out.println("ERROR: You must run USEI06 first (load stations and build AVL trees).");
            return;
        }

        KDtree tree = repo.getKdtree();
        tree.buildTree();

        System.out.println("\nUSEI07: KD-tree successfully built!");
    }

    /**
     * Shows basic KD-tree diagnostic information in the console.
     * If the KD-tree is not yet built, an informational message is printed.
     */
    public void showKDTreeStats() {
        KDtree tree = repo.getKdtree();

        if (tree.getRoot() == null) {
            System.out.println("KD-tree has not been built yet. Please run USEI07 first.");
            return;
        }

        tree.inspectTree();
    }
}
