package org.afv.arqueoutils.util;

import java.util.List;

public record ModInfo(String name, String version, List<ModInfo> children) {}