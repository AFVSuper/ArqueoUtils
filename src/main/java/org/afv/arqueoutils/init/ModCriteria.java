package org.afv.arqueoutils.init;

import net.minecraft.advancement.criterion.Criteria;
import org.afv.arqueoutils.criterion.ApplyScrollCriterion;
import org.afv.arqueoutils.criterion.InvisFrameCriterion;

public class ModCriteria {
    public static final InvisFrameCriterion INVIS_FRAME =
            Criteria.register("invis_frame_criterion", new InvisFrameCriterion());

    public static final ApplyScrollCriterion APPLY_SCROLL =
            Criteria.register("apply_scroll_criterion", new ApplyScrollCriterion());

    public static void init() {}
}