// Copyright (C) 2021-2022 Zukaritasu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

#pragma once

#include "jbase.hpp"

JNIFUNCTION(jboolean) 
Java_org_zuky_dialogs_FolderBrowserDialog_showDialog(JNIPARAMS, 
													 jstring root, 
													 jstring description, 
													 jint flags, 
													 jlong hwndParent
													);

