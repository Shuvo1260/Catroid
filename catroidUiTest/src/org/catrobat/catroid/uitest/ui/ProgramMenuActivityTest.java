/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class ProgramMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;

	public ProgramMenuActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		//UiTestUtils.createEmptyProject();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
		ProjectManager.getInstance().deleteCurrentProject();
	}

	public void testCostumeButtonTextChange() {
		createProject();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite("sprite1");
		solo.clickOnText("sprite1");
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		assertTrue("Text on costume button is not 'Costumes'", solo.searchText(solo.getString(R.string.costumes)));
		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.clickOnText("Background");
		assertTrue("Text on costume button is not 'Backgrounds'", solo.searchText(solo.getString(R.string.backgrounds)));
	}

	public void testSpriteChangeViaSpinner() {
		createProject();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		addNewSprite("sprite1");
		addNewSprite("sprite2");
		solo.clickOnText("Background");
		assertEquals("Current sprite is not 'Background'", "Background", ProjectManager.INSTANCE.getCurrentSprite()
				.getName());
		solo.clickOnText("Background", 0);
		solo.sleep(100);
		solo.clickOnText("sprite1");
		solo.sleep(500);
		assertEquals("Current sprite is not 'sprite1'", "sprite1", ProjectManager.INSTANCE.getCurrentSprite().getName());
		solo.clickOnText("sprite1", 0);
		solo.sleep(100);
		solo.clickOnText("sprite2");
		solo.sleep(500);
		assertEquals("Current sprite is not 'sprite2'", "sprite2", ProjectManager.INSTANCE.getCurrentSprite().getName());
	}

	public void testPlayButton() {
		createProject();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText("Background");
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in StageActivity", StageActivity.class);
	}

	public void testMenuItemSettings() {
		createProject();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText("Background");
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_settings));
		solo.assertCurrentActivity("Not in SettingsActivity", SettingsActivity.class);
	}

	public void testMainMenuButton() {
		createProject();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText("Background");
		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
				solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.PROJECTNAME1);

		Sprite spriteCat = new Sprite("Background");
		Script startScriptCat = new StartScript(spriteCat);
		Script scriptTappedCat = new WhenScript(spriteCat);
		Brick setXBrick = new SetXBrick(spriteCat, 50);
		Brick setYBrick = new SetYBrick(spriteCat, 50);
		Brick changeXBrick = new ChangeXByNBrick(spriteCat, 50);
		startScriptCat.addBrick(setYBrick);
		startScriptCat.addBrick(setXBrick);
		scriptTappedCat.addBrick(changeXBrick);

		spriteCat.addScript(startScriptCat);
		spriteCat.addScript(scriptTappedCat);
		project.addSprite(spriteCat);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteCat);
		ProjectManager.getInstance().setCurrentScript(startScriptCat);

		File imageFile = UiTestUtils.saveFileToProject(project.getName(), "catroid_sunglasses.png",
				org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses, getActivity(), UiTestUtils.FileTypes.IMAGE);

		ProjectManager projectManager = ProjectManager.getInstance();
		ArrayList<CostumeData> costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName("Catroid sun");
		costumeDataList.add(costumeData);
		projectManager.getFileChecksumContainer().addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());

		File soundFile = UiTestUtils.saveFileToProject(project.getName(), "longsound.mp3",
				org.catrobat.catroid.uitest.R.raw.longsound, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle("longsound");

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
	}

	private void addNewSprite(String spriteName) {
		solo.sleep(500);
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.waitForText(solo.getString(R.string.new_sprite_dialog_title));

		EditText addNewSpriteEditText = solo.getEditText(0);
		//check if hint is set
		assertEquals("Not the proper hint set", solo.getString(R.string.new_sprite_dialog_default_sprite_name),
				addNewSpriteEditText.getHint());
		assertEquals("There should no text be set", "", addNewSpriteEditText.getText().toString());
		solo.enterText(0, spriteName);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(200);
	}
}
